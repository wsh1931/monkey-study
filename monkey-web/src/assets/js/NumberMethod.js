
// 当数量超过1000时显示1k+，以此类推
function getFormatNumber(number) {
    if (number >= 10000) {
        return (number / 10000) + 'w+';
    }
    if (number >= 1000) {
        return (number / 1000) + 'k+';
    }
    return number;
}

export {
    getFormatNumber
}