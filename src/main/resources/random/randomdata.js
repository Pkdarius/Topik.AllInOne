const fs = require("fs");

const chars = "abcdefghijklmnopqrstuvwxyz";

const list = [];
const number = 3;
const length = 10;

for (let i = 0; i < number; i++) {
  let num = "";

  for (let j = 0; j < length; j++) {
    const n = Math.floor(Math.random() * chars.length);
    num += n % 10;
  }

  list.push({
    level: "8",
    emailName: "caubengocnghech",
    emailDomain: "gmail.com",
    fullName: "caubengocnghech",
    koreanName: "멍청한 소년",
    yearOfBirth: "2000",
    monthOfBirth: "1",
    dayOfBirth: "1",
    sex: "m",
    sid: num,
    job: "1",
    phone: num,
    address: "Trường Hàn Quốc Hà Nội, Mai Dịch, Cầu Giấy, Hà Nội",
    password: "caubengocnghech"
  });
}

fs.writeFileSync('../data.json', JSON.stringify(list));