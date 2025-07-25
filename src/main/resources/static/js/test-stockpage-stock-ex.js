
document.addEventListener('DOMContentLoaded', () => {
    const exValue    = document.getElementById('ex-value');
    const exUp       = document.getElementById('ex-up');
    const exDown     = document.getElementById('ex-down');
    //const exDividend = document.getElementById('ex-dividend');

    const tabs      = document.querySelectorAll('.exchange-tabs .ex-tab');
    let currentEx   = 'NASDAQ';
    const loadedMap = {};  // { NASDAQ: true, NYSE: true, … }

    function fetchListEx(exchange, category) {
        return fetch(
            `/api/proxy/naver-stock/ex?name=${exchange}&type=${category}&page=1&pageSize=20`
        )
            .then(res => {
                if (!res.ok) throw new Error(res.statusText);
                return res.json();
            })
            .then(json => typeof json === 'string' ? JSON.parse(json) : json)
            .then(data => {
                if (Array.isArray(data)) return data;
                if (data.stocks)      return data.stocks;
                if (data.data)        return data.data;
                return [];
            });
    }


    function fetchForeignStocks(exchange) {
        Promise.all([
            fetchListEx(exchange, 'marketValue'),
            fetchListEx(exchange, 'up'),
            fetchListEx(exchange, 'down'),
            fetchListEx(exchange, 'dividend')
        ])
            .then(([mv, up, down, div]) => {
                mv.sort((a, b) => {
                    const aCap = parseFloat((a.marketValue || "0").replace(/,/g, ""));
                    const bCap = parseFloat((b.marketValue || "0").replace(/,/g, ""));
                    return bCap - aCap;
                });

                exValue.innerHTML    = renderStockList(mv);
                exUp.innerHTML       = renderStockList(up);
                exDown.innerHTML     = renderStockList(down);
                // exDividend.innerHTML = renderStockList(div);
            })
            .catch(err => {
                console.error(`${exchange} 조회 오류:`, err);     // err 객체 전체를 찍음
                const msg = `<p class="error">조회 오류: ${err.message}</p>`;
                exValue.innerHTML = exUp.innerHTML = exDown.innerHTML = msg;
            });
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            currentEx = tab.dataset.ex;
            if (!loadedMap[currentEx]) {
                fetchForeignStocks(currentEx);
                loadedMap[currentEx] = true;
            }
        });
    });

    setInterval(() => {
        if (loadedMap[currentEx]) {
            fetchForeignStocks(currentEx);
        }
    }, 10000);

    // default : NASDAQ
    fetchForeignStocks(currentEx);
    loadedMap[currentEx] = true;
});
