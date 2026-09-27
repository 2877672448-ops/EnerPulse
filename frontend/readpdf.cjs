const fs = require("fs");
const { PDFParse } = require("pdf-parse");
const buf = fs.readFileSync("c:\\Users\\PANJU\\.trae-cn\\attachments\\6ab8b8981df3cdbd89daee7e\\cbd5b074-a2cf-46f8-9480-a7e64c973a0a_4b109fa6-dc38-4195-94de-f2cfbb4a8bd3_resume.pdf");
const u8 = new Uint8Array(buf.buffer, buf.byteOffset, buf.byteLength);
(async () => {
  const r = await new PDFParse({ data: u8 }).getText();
  console.log(r.text);
})();
