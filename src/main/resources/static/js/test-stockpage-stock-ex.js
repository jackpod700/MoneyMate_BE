document.addEventListener('DOMContentLoaded', () => {
    const exValue    = document.getElementById('ex-value');
    const exUp       = document.getElementById('ex-up');
    const exDown     = document.getElementById('ex-down');
    const exDividend = document.getElementById('ex-dividend');

    const tabs        = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');
    const exTabs      = document.querySelectorAll('.exchange-tabs .ex-tab');

    let currentEx     = 'NASDAQ';
    const loadedMap   = {};  // { NASDAQ: true, NYSE: true, … }

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
                if (Array.isArray(data))   return data;
                if (data.stocks)           return data.stocks;
                if (data.data)             return data.data;
                return [];
            });
    }

    function fetchForeignStocks(exchange) {
        const mainHeader = document.querySelector('.tab[data-tab="marketindex"]');
        if (!mainHeader || !mainHeader.classList.contains('active')) {
            return;
        }

        const exHeader = document.querySelector(`.exchange-tabs .ex-tab[data-ex="${exchange}"]`);
        if (!exHeader || !exHeader.classList.contains('active')) {
            return;
        }

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
                exDividend.innerHTML = renderStockList(div);
            })
            .catch(err => {
                console.error(`${exchange} 조회 오류:`, err);
                const msg = `<p class="error">조회 오류: ${err.message}</p>`;
                exValue.innerHTML = exUp.innerHTML = exDown.innerHTML = exDividend.innerHTML = msg;
            });
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.tab;

            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            tabContents.forEach(c =>
                c.classList.toggle('active', c.id === 'tab-' + target)
            );

            if (target === 'marketindex') {
                Object.keys(loadedMap).forEach(ex => {
                    fetchForeignStocks(ex);
                });
            }
        });
    });


    exTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            exTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            currentEx = tab.dataset.ex;
            if (!loadedMap[currentEx]) {
                loadedMap[currentEx] = true;
            }
            fetchForeignStocks(currentEx);
        });
    });

    setInterval(() => {
        Object.keys(loadedMap).forEach(ex => fetchForeignStocks(ex));
    }, 10000);


    const initEx = document.querySelector('.exchange-tabs .ex-tab[data-ex="NASDAQ"]');
    if (initEx) {
        initEx.classList.add('active');
        loadedMap['NASDAQ'] = true;
        fetchForeignStocks('NASDAQ');
    }
});
