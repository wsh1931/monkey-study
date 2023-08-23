// WebSocketService.js

class WebSocketService {
    constructor() {
      this.socket = null;
    }
  
    connect(url) {
      this.socket = new WebSocket(url);
      
      this.socket.onopen = () => {
        console.log("建立连接")
      };
      
      this.socket.onmessage = (event) => {
        // 执行接收后端消息的第二步
        const data = JSON.parse(event.data);
        this.onMessageCallback(data);
        // 执行接收后端消息的第四步
      };
      
    }
  
  send(data) {
    if (this.socket && this.socket.readyState == WebSocket.OPEN) {
        this.socket.send(JSON.stringify(data));
      }
    }
  
    onMessage(callback) {
      // 执行接收后端消息的第一步
      this.onMessageCallback = callback;
    }
  
    onClose() {
      if (this.socket) {
        this.socket.close();
      }
    }
  }


  export default new WebSocketService();