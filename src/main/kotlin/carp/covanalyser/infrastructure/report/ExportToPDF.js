const puppeteer = require('puppeteer');

(async () => {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    console.log('test')
    let status = await page.goto('http://localhost:63342/thesis/src/main/kotlin/carp/covanalyser/infrastructure/report/Heatmap.html?_ijt=d0b1vuk2ikch1h7r2d1ejv0p7g&_ij_reload=RELOAD_ON_SAVE', {waitUntil: 'networkidle2'});
    console.log(status)
    await page.pdf({path: 'report.pdf', format: 'A4'});
    console.log('test3')
    await browser.close();
})();
