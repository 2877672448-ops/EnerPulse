const fs = require("fs");
const pdfjs = require("pdfjs-dist/legacy/build/pdf.js");
async function main() {
  const buf = fs.readFileSync("c:\\Users\\PANJU\\.trae-cn\\attachments\\6ab8b8981df3cdbd89daee7e\\ddc60d68-c124-454b-bf36-7311f71d64b5_be47c396-5d56-47f6-80d6-5c2295bd7d8f_能源管理平台2.0使用说明书-Ch.pdf");
  const data = new Uint8Array(buf);
  const doc = await pdfjs.getDocument({ data }).promise;
  let allText = "";
  for (let i = 1; i <= doc.numPages; i++) {
    const page = await doc.getPage(i);
    const content = await page.getTextContent();
    const text = content.items.map((item) => item.str).join(" ");
    allText += "\n=== Page " + i + " ===\n" + text + "\n";
  }
  fs.writeFileSync("D:\\EnerFlow\\frontend\\pdf-full.txt", allText, "utf8");
  console.log("Total pages:", doc.numPages);
  console.log("Text saved to pdf-full.txt");
}
main().catch(console.error);