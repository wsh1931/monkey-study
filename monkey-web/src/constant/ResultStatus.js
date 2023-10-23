// import Enum from "./Enum";

// class ExceptionEnum extends Enum {
    
//     /**
//      * 枚举对象
//      */
//     static NORMAL = [1, '正常'];
//     static DISABLE = [0, '禁用'];
    
//     /**
//      * 状态编码
//      */
//     static code;
    
//     /**
//      * 状态描述
//      */
//     static desc;
    
// }

// const obj = new ExceptionEnum()

// export default obj

let ExceptionEnum = createEnum({
    SUCCESS: ['200', "操作成功，请等待数据刷新"],
    NOT_POWER: ['1600', '权限不足'],
    NOT_LOGIN: ['1700', '请先登录']
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


export default ExceptionEnum