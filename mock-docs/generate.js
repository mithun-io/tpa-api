const puppeteer = require('puppeteer');
const path = require('path');

async function generateDocs() {
  console.log('Launching browser...');
  const browser = await puppeteer.launch({ headless: 'new' });
  const page = await browser.newPage();
  
  // Set viewport to A4 size roughly
  await page.setViewport({ width: 794, height: 1123, deviceScaleFactor: 2 });

  const docs = ['claim-form', 'hospital-bill'];

  for (const doc of docs) {
    console.log(`Processing ${doc}...`);
    const filePath = `file://${path.resolve(__dirname, `${doc}.html`)}`;
    
    // Go to HTML file and wait for fonts to load
    await page.goto(filePath, { waitUntil: 'networkidle0' });
    
    // Generate PDF
    await page.pdf({
      path: path.resolve(__dirname, `${doc}.pdf`),
      format: 'A4',
      printBackground: true,
      margin: { top: '0', right: '0', bottom: '0', left: '0' }
    });
    console.log(`Saved ${doc}.pdf`);

    // Generate PNG
    await page.screenshot({
      path: path.resolve(__dirname, `${doc}.png`),
      fullPage: true
    });
    console.log(`Saved ${doc}.png`);
  }

  await browser.close();
  console.log('Done!');
}

generateDocs().catch(console.error);
