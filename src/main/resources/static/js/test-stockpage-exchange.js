    document.addEventListener('DOMContentLoaded', () => {
        const tabs = document.querySelectorAll('.tab');
        const tabContents = document.querySelectorAll('.tab-content');

        // 이름(name) 기준으로 걸러낼 6개
        const showNames = [
            '미국 USD', '유럽 EUR', '일본 JPY',
            '중국 CNY', '영국 GBP', '홍콩 HKD'
        ];
        let isExpanded = false;
        let count = 0;

        function formatItem(item) {
            const val = parseFloat(item.fluctuations);
            const pct = parseFloat(item.fluctuationsRatio);
            let arrow, cls;
            if      (val > 0) { arrow = '▲'; cls = 'up'; }
            else if (val < 0) { arrow = '▼'; cls = 'down'; }
            else              { arrow = '='; cls = 'neutral'; }
            const changeText = val !== 0 ? ` ${Math.abs(val).toFixed(4)}` : '';
            return `
          <li class="exchange-item ${cls}">
            <span class="currency">${item.name}</span>
            <span class="price">${item.closePrice}</span>
            <span class="change">${arrow}${changeText} (${pct.toFixed(2)}%)</span>
          </li>`;
        }

        function renderPrimaryList(items) {
            if (!items.length) {
                return '<p>주요 환율 데이터가 없습니다.</p>';
            }
            return `
          <ul class="exchange-list">
            ${items.map(formatItem).join('')}
          </ul>
        `;
        }

        function renderToggleButton() {
            return `<button id="toggle-secondary" class="toggle-btn-300">
          ${isExpanded ? '▲ 접기' : '▼ 기타 환율 펼치기'}
        </button>`;
        }

        function renderSecondaryList(items) {
            const hidden = isExpanded ? '' : ' hidden';
            return `
          <ul id="secondary-list" class="exchange-list${hidden}">
            ${items.map(formatItem).join('')}
          </ul>
        `;
        }

        function fetchExchangeData() {
            const marketTab = document.getElementById('tab-marketindex');
            if (!marketTab.classList.contains('active')) return;

            fetch('/api/proxy/naver-stock/exchange')
                .then(res => res.json())
                .then(json => {
                    const list = json.result || [];
                    const primaryContainer = document.getElementById('exchange-primary');
                    const extraContainer   = document.getElementById('exchange-extra');
                    primaryContainer.innerHTML = '';
                    extraContainer.innerHTML  = '';

                    if (!list.length) {
                        primaryContainer.innerHTML = '<p>환율 데이터가 없습니다.</p>';
                        return;
                    }

                    // 이름(name) 기준 필터 + 정해진 순서대로 정렬
                    const primaryItems = list
                        .filter(i => showNames.includes(i.name))
                        .sort((a, b) => showNames.indexOf(a.name) - showNames.indexOf(b.name));

                    const secondaryItems = list.filter(i => !showNames.includes(i.name));

                    primaryContainer.innerHTML = renderPrimaryList(primaryItems);

                    if (secondaryItems.length) {
                        extraContainer.innerHTML =
                            renderToggleButton() +
                            renderSecondaryList(secondaryItems);

                        const btn      = document.getElementById('toggle-secondary');
                        const moreList = document.getElementById('secondary-list');
                        btn.addEventListener('click', () => {
                            isExpanded = !isExpanded;
                            moreList.classList.toggle('hidden', !isExpanded);
                            btn.textContent = isExpanded ? '▲ 접기' : '▼ 기타 환율 펼치기';
                        });
                    }
                })
                .catch(err => console.error('환율 조회 오류:', err));
        }


        // 탭 클릭 시 바로 데이터 로드
        tabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const targetId = 'tab-' + tab.dataset.tab; // 'tab-marketindex'
                tabs.forEach(t => t.classList.remove('active'));
                tab.classList.add('active');
                tabContents.forEach(c => c.classList.toggle('active', c.id === targetId));

                if ((targetId === 'tab-marketindex')&&(count < 1)) {
                    fetchExchangeData();
                    count++;
                }
            });
        });

        // 최초 로드 및 10초마다 갱신
        setInterval(fetchExchangeData, 10000);
    });