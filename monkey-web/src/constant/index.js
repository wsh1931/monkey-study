import ResultStatus from '@/constant/ResultStatus'
import messageType from './MessageType'
import exceptionMessage from './ExceptionConstant';
import reportContentType from './ReportContentType';
import reportCommentTypes from './ReportCommentType';
import FormTypeEnum from './FormTypeEnum';
export default {
  install(Vue) {
    Vue.prototype.ResultStatus = ResultStatus,
    Vue.prototype.messageType = messageType;
    Vue.prototype.exceptionMessage = exceptionMessage;
    Vue.prototype.reportContentType = reportContentType;
    Vue.prototype.reportCommentTypes = reportCommentTypes
    Vue.prototype.formType = FormTypeEnum
  }
}
