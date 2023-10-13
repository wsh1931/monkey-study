// 将日期字符串 2023-10-12 08:41:11 转化为 几秒前，几天前。。。格式
function getTimeFormat(time) {
    const timestamp = Date.parse(time);
    const now = Date.now();
    const seconds = Math.floor((now - timestamp) / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);
    const months = Math.floor(days / 30);
    const years = Math.floor(days / 365);

    if (years > 0) {
        return years + "年前";
    } else if (months > 0) {
        return months + "月前"
    } else if (days > 0) {
        return days + "天前";
    } else if (hours > 0) {
        return hours + "小时前";
    } else if (minutes > 0) {
        return minutes + "分钟前";
    } else {
        return "刚刚";
    }
}
export {
    getTimeFormat,
}