let SearchTypeEnum = createEnum({
    ARTICLE: ['1', "文章"],
    QUESTION: ['2', '问答'],
    COURSE: ['3', '课程'],
    COMMUNITY_ARTICLE: ['4', '社区文章'],
    RESOURCE: ['5', '资源'],
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


export default SearchTypeEnum