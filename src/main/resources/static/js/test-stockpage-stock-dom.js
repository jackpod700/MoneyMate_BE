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
    </ul>` + '<div class="div-description">실시간 시세, 1분마다 자동으로 갱신됩니다.</div>';
}

function formatIndustry(item) {
    const rate = parseFloat(item.changeRate);
    const arrow = rate > 0 ? '▲' : rate < 0 ? '▼' : '=';
    const cls   = rate > 0 ? 'up' : rate < 0 ? 'down' : 'neutral';

    const riseCount   = item.riseCount   ?? 0;
    const fallCount   = item.fallCount   ?? 0;
    const steadyCount = item.steadyCount ?? 0;

    return `
      <li class="exchange-item ${cls}">
        <span class="currency">${item.name} </span>
        <span class="riseCount"> ▲${riseCount} </span>
        <span class="fallCount"> ▼${fallCount} </span>
        <span class="steadyCount"> =${steadyCount}     </span>
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
    </ul>` + '<div class="div-description">실시간 시세, 1분마다 자동으로 갱신됩니다.</div>';
}

function fetchProxy(path, params) {
    const qs = new URLSearchParams(params).toString();
    return fetch(`${path}?${qs}`)
        .then(res => {
            if (!res.ok) throw new Error(res.statusText);
            return res.text();
        })
        .then(txt => JSON.parse(txt));
}

function fetchList(type, page = 1, pageSize = 20) {
    return fetchProxy('/api/proxy/naver-stock/domestic', { type, page, pageSize })
        .then(json => {
            if (json.stocks)  return json.stocks;
            if (json.result)  return json.result;
            return json;
        });
}

function fetchIndustry(page = 1, pageSize = 20) {
    return fetchProxy('/api/proxy/naver-stock/domestic', { type: 'industry', page, pageSize })
        .then(json => json.groups || []);
}

function fetchDomesticStocks() {
    const marketTab = document.getElementById('tab-marketindex');
    if (!marketTab.classList.contains('active')) return;

    Promise.all([
        fetchList('market'),
        fetchList('up'),
        fetchList('down'),
        Promise.resolve([]),
        Promise.all([1,2,3,4].map(p => fetchIndustry(p))).then(arr => arr.flat())
    ])
        .then(([marketList, upList, downList, dividendList, industryGroups]) => {
            document.getElementById('dom-value').innerHTML    = renderStockList(marketList);
            document.getElementById('dom-up').innerHTML       = renderStockList(upList);
            document.getElementById('dom-down').innerHTML     = renderStockList(downList);
            document.getElementById('dom-dividend').innerHTML = renderStockList(dividendList);
            document.getElementById('dom-industry').innerHTML = renderIndustryList(industryGroups);
        })
        .catch(err => {
            console.error('국내 주식 조회 오류:', err);
            const msg = `<p class="error">조회 오류: ${err.message}</p>`;
            ['dom-value','dom-up','dom-down','dom-industry'].forEach(id => {
                document.getElementById(id).innerHTML = msg;
            });
        });
}

document.addEventListener('DOMContentLoaded', () => {
    const tabs        = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    let countDom = 0;

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetId = 'tab-' + tab.dataset.tab;
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            tabContents.forEach(c => c.classList.toggle('active', c.id === targetId));

            if (targetId === 'tab-marketindex' && countDom < 1) {
                fetchDomesticStocks();
                countDom++;
            }
        });
    });

    // 10초마다 자동 갱신
    setInterval(fetchDomesticStocks, 60000);
});
