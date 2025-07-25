
function formatStock(item) {
    const nameKor  = item.stockName || '';
    const exchCode = item.stockExchangeType?.name || '';
    const displayName = exchCode
        ? `${nameKor} (${exchCode})`
            : nameKor;

    const price = item.closePrice
        || (item.now && item.now.tradePrice)
        || '';

    const changeVal = parseFloat(
        item.compareToPreviousClosePrice
        || item.now?.compareToPreviousClosePrice
        || 0
    );
    const pct = parseFloat(
        item.fluctuationsRatio
        || item.now?.fluctuationsRatio
        || 0
    );

    const arrow = changeVal > 0 ? '▲' : changeVal < 0 ? '▼' : '=';
    const cls   = changeVal > 0 ? 'up' : changeVal < 0 ? 'down' : 'neutral';

    return `
      <li class="exchange-item ${cls}">
        <span class="currency">${displayName}</span>
        <span class="price">${price}</span>
        <span class="change">
          ${arrow}${Math.abs(changeVal).toFixed(2)} (${pct.toFixed(2)}%)
        </span>
      </li>`;
}

function renderStockList(list) {
    if (!Array.isArray(list) || list.length === 0) {
        return '<p>데이터가 없습니다.</p>';
    }
    return `
      <ul class="exchange-list">
        ${list.slice(0, 20).map(formatStock).join('')}
      </ul>`;
}















document.addEventListener('DOMContentLoaded', () => {
    const tabs             = document.querySelectorAll('.tab');
    const tabContents      = document.querySelectorAll('.tab-content');
    const domValueContainer    = document.getElementById('dom-value');
    const domUpContainer       = document.getElementById('dom-up');
    const domDownContainer     = document.getElementById('dom-down');
    const domIndustryContainer = document.getElementById('dom-industry');

    let countDom = 0;

    function formatIndustry(item) {
        const rate = parseFloat(item.changeRate);
        const arrow = rate > 0 ? '▲' : rate < 0 ? '▼' : '=';
        const cls   = rate > 0 ? 'up' : rate < 0 ? 'down' : 'neutral';

        return `
      <li class="exchange-item ${cls}">
        <span class="currency">${item.name}</span>
        <span class="change">${arrow}${Math.abs(rate).toFixed(2)}%</span>
      </li>`;
    }

    function renderIndustryList(groups) {
        if (!Array.isArray(groups) || groups.length === 0) {
            return '<p>업종 데이터가 없습니다.</p>';
        }
        return `
      <ul class="exchange-list">
        ${groups.map(formatIndustry).join('')}
      </ul>`;
    }


    function fetchList(type) {
        return fetch(`/api/proxy/naver-stock/domestic?type=${type}&page=1&pageSize=20`)
            .then(res => {
                if (!res.ok) throw new Error(res.statusText);
                return res.json();
            })
            .then(json => {
                let list = typeof json === 'string' ? JSON.parse(json) : json;
                if (list.stocks) list = list.stocks;
                if (list.result) list = list.result;
                return list;
            });
    }

    function fetchIndustry() {
        // 1~4페이지를 병렬로 요청
        const pages = [1, 2, 3, 4];
        const requests = pages.map(page =>
            fetch(`/api/proxy/naver-stock/domestic?type=industry&page=${page}&pageSize=20`)
                .then(res => {
                    if (!res.ok) throw new Error(res.statusText);
                    return res.json();
                })
                .then(json => {
                    const obj = typeof json === 'string' ? JSON.parse(json) : json;
                    return obj.groups || [];
                })
        );

        return Promise.all(requests)
            .then(pageGroups => pageGroups.flat());
    }


    function fetchDomesticStocks() {
        const marketTab = document.getElementById('tab-marketindex');
        if (!marketTab.classList.contains('active')) return;

        Promise.all([
            fetchList('market'),
            fetchList('up'),
            fetchList('down'),
            fetchIndustry()
        ])
            .then(([marketList, upList, downList, industryGroups]) => {
                domValueContainer.innerHTML    = renderStockList(marketList);
                domUpContainer.innerHTML       = renderStockList(upList);
                domDownContainer.innerHTML     = renderStockList(downList);
                domIndustryContainer.innerHTML = renderIndustryList(industryGroups);
            })
            .catch(err => {
                console.error('국내 주식 조회 오류:', err);
                const msg = `<p class="error">조회 오류: ${err.message}</p>`;
                domValueContainer.innerHTML    = msg;
                domUpContainer.innerHTML       = msg;
                domDownContainer.innerHTML     = msg;
                domIndustryContainer.innerHTML = msg;
            });
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetId = 'tab-' + tab.dataset.tab;
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            tabContents.forEach(c =>
                c.classList.toggle('active', c.id === targetId)
            );

            if (targetId === 'tab-marketindex' && countDom < 1) {
                fetchDomesticStocks();
                countDom++;
            }
        });
    });

    setInterval(fetchDomesticStocks, 10000);
});
