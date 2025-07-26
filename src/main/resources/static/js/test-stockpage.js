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
                  [ERROR]: 다음 입력은 사용할 수 없습니다.
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
        if (ticker.includes('<') || ticker.includes('>')) {
            resultBox.classList.add('show');
            resultBox.innerHTML = `
              <div class="error-box">
                [ERROR]: 다음 입력은 사용할 수 없습니다.
              </div>
            `;
            return;
        }


        const symbol = `${ticker}.${market}`;
        // const url = `https://eodhd.com/api/real-time/${symbol}?api_token=-------&fmt=json`;
        const proxyUrl = `/api/proxy/eodhd/realtime/15min`
            + `?ticker=${encodeURIComponent(ticker)}`
            + `&market=${encodeURIComponent(market)}`;

        resultBox.classList.add('show');
        resultBox.innerHTML = `<p> <strong>${symbol}</strong> 시세 조회 중...</p>`;

        fetch(proxyUrl)
            .then(res => {
                if (res.status === 500) {
                    resultBox.innerHTML = `
                      <div class="error-box">
                        기타 오류가 발생했습니다.
                      </div>`;
                    return Promise.reject();
                }
                if (res.status === 409) {
                    resultBox.innerHTML = `
                      <div class="error-box">
                        거래소와 종목 코드를 다시 확인해 주세요
                      </div>`;
                    return Promise.reject();
                }
                if (!res.ok) {
                    resultBox.innerHTML = `
                      <div class="error-box">
                        오류가 발생했습니다. (코드: ${res.status})
                      </div>`;
                    return Promise.reject();
                }
                return res.json();
            })
            .then(data => {
                const ts = data.timestamp + (data.gmtoffset || 0);
                const date = new Date(ts * 1000);
                const formattedDate = date.toLocaleDateString('ko-KR', {
                    year:  'numeric',
                    month: '2-digit',
                    day:   '2-digit'
                });
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
                    <div class="timestamp">${formattedDate} ${formattedTime} 기준 <br></div>
                    <h3>${symbol}</h3>
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
    const bulkBtn    = document.getElementById('bulk-fetch-btn');
    const bulkMarket = document.getElementById('bulk-market');
    const bulkResult = document.getElementById('bulk-result');

    if (bulkBtn) {
        bulkBtn.addEventListener('click', () => {
            const market = bulkMarket.value;
            const proxyUrl = `/api/proxy/eodhd/bulk?market=${encodeURIComponent(market)}`;

            bulkResult.classList.add('show');
            bulkResult.innerHTML = `🔄 ${market} 전체 종목 시세 조회 중...`;

            fetch(proxyUrl)
                .then(res => {
                    if (res.status === 500) {
                        bulkResult.innerHTML = `
                        <div class="error-box">
                          기타 오류가 발생했습니다.
                        </div>`;
                        return Promise.reject();
                    }
                    if (res.status === 409) {
                        bulkResult.innerHTML = `
                        <div class="error-box">
                          기타 오류가 발생했습니다.
                        </div>`;
                        return Promise.reject();
                    }
                    if (!res.ok) {
                        bulkResult.innerHTML = `
                        <div class="error-box">
                          오류가 발생했습니다. (코드: ${res.status})
                        </div>`;
                        return Promise.reject();
                    }
                    return res.json();
                })
                .then(data => {
                    if (!Array.isArray(data)) {
                        bulkResult.innerHTML = `<div class="error-box">[ERROR] API 오류: ${JSON.stringify(data)}</div>`;
                        return;
                    }
                    const limitedData = data.slice(0, 65535);
                    let html = `<table><thead><tr>
                      <th>종목코드</th><th>시가</th><th>종가</th><th>고가</th><th>저가</th>
                    </tr></thead><tbody>`;
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
                });
        });
    }
});



