import ResultStatus from '@/constant/ResultStatus'
import messageType from './MessageType'
export default {
  install(Vue) {
      Vue.prototype.ResultStatus = ResultStatus,
      Vue.prototype.messageType = messageType;
  }
}
