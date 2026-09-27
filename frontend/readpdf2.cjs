const fs = require("fs");
const { PDFParse } = require("pdf-parse");
const buf = fs.readFileSync("c:\\Users\\PANJU\\.trae-cn\\attachments\\6ab8b8981df3cdbd89daee7e\\6ad5dce9-62b4-48e3-aaab-6a26c1b28a88_411e61a0-fe03-45b7-8f7a-1c632023ca91_能源管理平台2.0使用说明书-Ch.pdf");
const u8 = new Uint8Array(buf.buffer, buf.byteOffset, buf.byteLength);
(async () => {
  const r = await new PDFParse({ data: u8 }).getText();
  console.log(r.text);
})();
