
let FormTypeEnum = createEnum({
    FORM_TYPE_ALL: ['-1', "全部"],
    FORM_TYPE_FREE: ['1', '免费'],
    FORM_TYPE_VIP: ['2', '会员免费'],
    FORM_TYPE_TOLL: ["3", "收费"],
    FORM_TYPE_COMMEND: ["4", "官方推荐"]
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