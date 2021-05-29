var cleaveCustom = new Cleave('.cnic', {
    blocks: [5,7,1],
    delimiter: '-',
});
var cleavePhone = new Cleave('.phone', {
    blocks: [4,7],
    delimiter: '-',
});