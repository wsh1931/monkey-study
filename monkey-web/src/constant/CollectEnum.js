
let FormTypeEnum = createEnum({
    COLLECT_ARTICLE: ['0', "文章收藏"],
    COLLECT_QUESTION: ['1', "问答收藏"],
    COLLECT_COURSE: ['2', '课程收藏'],
    COLLECT_COMMUNITY_ARTICLE: ["3", "社区文章收藏"],
    COLLECT_RESOURCE: ["4", "资源收藏"]
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


export default FormTypeEnum