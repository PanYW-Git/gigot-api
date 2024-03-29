// authorization.js

Page({
  // 点击授权按钮触发的事件
  data: {
    scene: '', // 将scene存储在Page数据中
    scanStep: 1
  },
  onLoad(options) {
    console.log(options.scene);
		if(options !== undefined) {
			if(options.scene) {
        // 获取scene
        this.setData({
          scanStep: 2,
          loginSuccess: true
        })
        let scene = decodeURIComponent(options.scene);
        console.log("接收到扫码登录的scene:" + scene);
        this.setData({
          scene: scene
        });
      }
    }
  },

  authorize: function () {
    // 调用微信的授权接口，获取用户信息
    wx.login({
      success: (res) => {
        desc: '用户登录',
        // 获取用户信息成功后，发送用户信息给后端
        console.log("成功登录");
        this.sendLoginToServer(res);
      },
      fail: (err) => {
        console.error(err);
      }
    })
  },
  jump: function () {
    console.log("点了哦");
    wx.redirectTo({
      url: '../test/test',
    })
  },

  // 将用户信息发送给后端
  sendLoginToServer: function (loginInfo) {
    // 发送网络请求，将用户信息传递给后端
    wx.showToast({
      title: '正在发送...',
      icon: 'loading', // 图标，可选值：'success', 
    })
    wx.request({
      // url: 'http://localhost:8001/api/user/login/xcx', // 替换为实际的后端接口地址
      url: 'https://api.panyuwen.top/api/user/login/xcx', // 替换为实际的后端接口地址
      method: 'POST',
      data: {
        code: loginInfo.code,
        scene: this.data.scene,
      },
      success: (res) => {
        console.log(res.data);
        if(res.data.code === 0){
					this.setData({
						scanStep: 3,
						loginSuccess: true
					})
          wx.showToast({
            title: '登录成功',
            icon: 'success', // 图标，可选值：'success', 
          })
        }
        // 处理后端返回的数据，例如登录成功后的逻辑处理
      },
      fail: (err) => {
        console.error(err);
      }
    });
  }
});
