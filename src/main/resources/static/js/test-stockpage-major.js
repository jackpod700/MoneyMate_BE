document.addEventListener('DOMContentLoaded', () => {
    const tabs        = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    const container   = document.getElementById('index-show');

    let count = 0;

    /**
     * 환율과 동일한 방법 사용
     * @param item
     * @returns {string}
     */
    function formatIndex(item) {
        const price  = item.closePrice;
        const change = parseFloat(item.compareToPreviousClosePrice);
        const pct    = parseFloat(item.fluctuationsRatio);
        const arrow  = change > 0 ? '▲' : change < 0 ? '▼' : '=';
        const cls    = change > 0 ? 'up' : change < 0 ? 'down' : 'neutral';

        return `
      <li class="exchange-item ${cls}">
        <span class="currency">${item.indexName}</span>
        <span class="price">${price}</span>
        <span class="change">
          ${arrow}${Math.abs(change).toFixed(2)} (${pct.toFixed(2)}%)
        </span>
      </li>`;
    }

    function renderIndexList(list) {
        if (!list || !list.length) {
            return '<p>지수 데이터가 없습니다.</p>';
        }
        return `
      <ul class="exchange-list">
        ${list.map(formatIndex).join('')}
      </ul>`;
    }

    function fetchIndexData() {
        const activeTab = document.getElementById('tab-marketindex');
        if (!activeTab.classList.contains('active')) return;

        fetch('/api/proxy/naver-stock/index?nation=MAJOR')
            .then(res => {
                if (!res.ok) throw new Error(res.statusText);
                return res.json();
            })
            .then(json => {
                const list = typeof json === 'string' ? JSON.parse(json) : json;
                container.innerHTML =
                    renderIndexList(list) +
                    '<div class="div-description">실시간 시세, 10초마다 자동으로 갱신됩니다.</div>';
            })
            .catch(err => {
                console.error('지수 조회 오류:', err);
                container.innerHTML = `<p class="error">조회 오류: ${err.message}</p>`;
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

            if (targetId === 'tab-marketindex' && count < 1) {
                fetchIndexData();
                count++;
            }
        });
    });

    setInterval(fetchIndexData, 10000);
});
