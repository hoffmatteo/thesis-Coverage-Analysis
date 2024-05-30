const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

async function exportChartToPdf() {
    const browser = await puppeteer.launch();
    const page = await browser.newPage();

    // Serve the local file
    const filePath = path.resolve(__dirname, 'Heatmap.html');
    const content = fs.readFileSync(filePath, 'utf8');
    await page.setContent(content, {waitUntil: 'load'});

    // Wait for the chart to render
    await page.waitForSelector('#my_dataviz', {timeout: 60000}); // Increase timeout to 60 seconds

    // Define the PDF options
    const pdfOptions = {
        path: 'chart.pdf',
        format: 'A4',
        printBackground: true,
        landscape: true
    };

    // Export the page to a PDF
    await page.pdf(pdfOptions);

    await browser.close();
}

exportChartToPdf().then(() => {
    console.log('PDF generated successfully.');
}).catch(err => {
    console.error('Error generating PDF:', err);
});
