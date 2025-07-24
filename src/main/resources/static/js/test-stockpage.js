document.addEventListener('DOMContentLoaded', function () {

    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.dataset.tab;

            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            tabContents.forEach(content => {
                content.classList.remove('active');
                if (content.id === 'tab-' + target) {
                    content.classList.add('active');
                }
            });
        });
    });

    const form = document.getElementById('stockForm');
    const resultBox = document.getElementById('result-box');
    const submitBtn = form.querySelector('button[type="submit"]');


    form.addEventListener('submit', function (e) {
        e.preventDefault();
        // 버튼 1초간 비활성화
        submitBtn.disabled = true;
        setTimeout(() => { submitBtn.disabled = false; }, 1000);

        const rawInput = document.getElementById('symbol').value;

        if (/[<>]/.test(rawInput)) {
            resultBox.innerHTML = `
                <div class="error-box">
                  다음 입력은 사용할 수 없습니다. [XSS 방지]
                </div>
              `;
            submitBtn.disabled = false;
            return;
        }

        const market = document.getElementById('market').value;
        const ticker = document.getElementById('ticker').value.trim().toUpperCase();

        if (!ticker) {
            alert('티커를 입력하세요.');
            return;
        }

        const symbol = `${ticker}.${market}`;
        const url = `https://eodhd.com/api/real-time/${symbol}?api_token=687f9cc717eea0.26361602&fmt=json`;

        resultBox.classList.add('show');
        resultBox.innerHTML = `<p> <strong>${symbol}</strong> 시세 조회 중...</p>`;

        fetch(url)
            .then(res => res.json())
            .then(data => {
                const ts = data.timestamp + (data.gmtoffset || 0);
                const date = new Date(ts * 1000);
                const formattedTime = date.toLocaleTimeString('ko-KR', {
                    hour:   '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });

                /**
                 * 화살표 방향과 색상 결정
                 * @type {string}
                 */
                let arrow = '', arrowClass = '';
                if (data.change > 0) {
                    arrow = '▲'; arrowClass = 'up';
                } else if (data.change < 0) {
                    arrow = '▼'; arrowClass = 'down';
                }


                resultBox.innerHTML = `
                <div class="result-card">
                    <div class="timestamp">${formattedTime}</div>
                    <h2>${symbol}</h2>
                    <p><strong>시가:</strong> ${data.open}</p>
                    <p><strong>종가:</strong> ${data.close}</p>
                    <p><strong>고가:</strong> ${data.high}</p>
                    <p><strong>저가:</strong> ${data.low}</p>
                    <p class="change-line">
                        <strong>전일 대비:</strong>
                        <span class="arrow ${arrowClass}">${arrow}</span>
                        ${Math.abs(data.change).toFixed(2)} (${data.change_p.toFixed(2)}%)
                    </p>
                </div>
            `;

            })
            .catch(err => {
                console.error(err);
                resultBox.innerHTML = `
                    <div class="error-box">
                        조회 중 오류 발생: ${err}
                    </div>
                `;
            });
    });

    // Bulk 조회
    const bulkBtn = document.getElementById('bulk-fetch-btn');
    const bulkMarket = document.getElementById('bulk-market');
    const bulkResult = document.getElementById('bulk-result');

    if (bulkBtn) {
        bulkBtn.addEventListener('click', () => {
            const market = bulkMarket.value;
            const url = `https://eodhd.com/api/eod-bulk-last-day/${market}?api_token=687f9cc717eea0.26361602&fmt=json`;

            bulkResult.classList.add('show');
            bulkResult.innerHTML = `🔄 ${market} 전체 종목 시세 조회 중...`;

            fetch(url)
                .then(res => res.json())
                .then(data => {
                    if (!Array.isArray(data)) {
                        bulkResult.innerHTML = `<div class="error-box">[ERROR] API 오류: ${JSON.stringify(data)}</div>`;
                        return;
                    }

                    const limitedData = data.slice(0, 65535);
                    let html = `<table><thead><tr>
                        <th>종목코드</th><th>시가</th><th>종가</th><th>고가</th><th>저가</th></tr></thead><tbody>`;
                    for (const item of limitedData) {
                        const open = item.open >= 999999 ? '999999+' : item.open;
                        const close = item.close >= 999999 ? '999999+' : item.close;
                        const high = item.high >= 999999 ? '999999+' : item.high;
                        const low = item.low >= 999999 ? '999999+' : item.low;

                        html += `<tr>
                            <td>${item.code}</td>
                            <td>${open}</td>
                            <td>${close}</td>
                            <td>${high}</td>
                            <td>${low}</td>
                        </tr>`;
                    }
                    html += `</tbody></table>`;
                    bulkResult.innerHTML = html;
                })
                .catch(err => {
                    console.error(err);
                    bulkResult.innerHTML = `<div class="error-box">[ERROR] 오류 발생: ${err}</div>`;
                });
        });
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('naverRealtimeForm');
    const resultDiv = document.getElementById('naverRealtimeResult');
    const nSubmit = form.querySelector('button[type="submit"]');

    form.addEventListener('submit', function (e) {
        e.preventDefault();

        // 버튼 1초간 disabled
        nSubmit.disabled = true;
        setTimeout(() => { nSubmit.disabled = false; }, 1000);

        const rawInput = document.getElementById('symbol').value;

        if (/[<>]/.test(rawInput)) {
            resultDiv.innerHTML = `
                <div class="error-box">
                  다음 입력은 사용할 수 없습니다. [XSS 방지]
                </div>
              `;
            nSubmit.disabled = false;
            return;
        }


        const marketValue = document.getElementById('marketSelect').value;
        const [region, exchange] = marketValue.split('_');
        const ticker = document.getElementById('symbol').value.trim().toUpperCase();

        if (!ticker) {
            alert("티커를 입력하세요.");
            return;
        }

        // Spring Boot 서버에 프록시 요청
        const proxyUrl = `/api/naver/realtime?region=${region}&exchange=${exchange}&ticker=${ticker}`;

        resultDiv.classList.add('show');
        resultDiv.innerText = "🔄 실시간 시세 조회 중...";

        fetch(proxyUrl)
            .then(response => {
                if (!response.ok) throw new Error("서버 응답 오류");
                return response.json();
            })
            .then(data => {
                // NaN 상태로 그대로 포맷 출력 방지
                const closeRaw = data.closePrice ?? data.closePrice?.value;
                if (closeRaw === undefined || closeRaw === null || closeRaw === "") {
                    resultDiv.innerHTML = `
                        <div class="error-box">
                          오류: 거래소와 종목명을 다시 확인해 주세요
                        </div>
                      `;
                    return;
                }

                const tradedAt = new Date(data.localTradedAt);
                const formattedTime = tradedAt.toLocaleTimeString('ko-KR', {
                    hour:   '2-digit',
                    minute: '2-digit',
                    second: '2-digit'
                });

                const realTimeLabel = data.delayTimeName || '';

                const changeValue   = parseFloat(data.compareToPreviousClosePrice);
                const changePercent = parseFloat(data.fluctuationsRatio);
                let arrow = '', arrowClass = '';
                if (changeValue > 0) {
                    arrow      = '▲';
                    arrowClass = 'up';
                } else if (changeValue < 0) {
                    arrow      = '▼';
                    arrowClass = 'down';
                }

                const close = data.closePrice || data.closePrice?.value;
                const stockName = data.stockName || ticker;
                const market = data.stockExchangeName || '해외';

                resultDiv.innerHTML = `
                  <div class="result-card">
                    <div class="timestamp">${formattedTime} ${realTimeLabel}</div>
                    <h3>${stockName} (${ticker})</h3>
                    <p><strong>거래소:</strong> ${market}</p>
                    <p><strong>종가:</strong> ${close}</p>
                    <p class="change-line">
                      <strong>전일 대비:</strong>
                      <span class="arrow ${arrowClass}">${arrow}</span>
                      ${Math.abs(changeValue).toFixed(2)} (${changePercent.toFixed(2)}%)
                    </p>
                  </div>
                `;
            })
            .catch(err => {
                console.error(err);
                resultDiv.innerHTML = `<div class="error-box">[ERROR] 오류 발생: ${err}</div>`;
            });
    });
});
