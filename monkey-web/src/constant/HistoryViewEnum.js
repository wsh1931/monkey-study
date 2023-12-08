
let HistoryViewEnum = createEnum({
    ARTICLE: ['0', "文章"],
    QUESTION: ['1', "问答"],
    COURSE: ['2', '课程'],
    COMMUNITY_ARTICLE: ["3", "社区文章"],
    RESOURCE: ["4", "资源"]
});

function createEnum(definition) {
    const valueMap = {};
    const descMap = {};
    for (const key of Object.keys(definition)) {
        const [value, desc] = definition[key];
        valueMap[key] = value;
        descMap[value] = desc;
    }
    return {
        ...valueMap,
        getCode(key) {
            return (definition[key] && definition[key][1]) || '无';
        },
        getMsg(value) {
            return descMap[value] || '无';
        }
    }
}


export default HistoryViewEnum