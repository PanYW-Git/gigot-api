export default [
  { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    access: 'canAdmin',
    routes: [
      {
        name: '用户管理',
        icon: undefined,
        path: '/admin/userManage',
        component: './Admin/UserManage/index.tsx',
      },
      {
        name: '接口管理',
        icon: 'ApiOutlined',
        path: '/admin/interfaceinfoManage',
        component: './Admin/InterfaceInfo/index.tsx',
      },
    ],
  },

  { path: '/ListOpenApi', name: '接口市集', icon: 'ApiOutlined', component: './ListOpenApi' },
  {
    path: '/ListOpenApi/info/:id',
    name: '查看接口文档',
    component: './ListOpenApi/info',
    hideInMenu: true,
  },
  { path: '/ProductInfo', name: '充值服务', icon: 'PayCircleOutlined', component: './ProductInfo' },
  {
    path: '/ProductInfo/pay/:id',
    icon: 'PayCircleOutlined',
    name: '订单支付',
    component: './ProductInfo/pay',
    hideInMenu: true,
  },

  { path: '/ProductOrder', name: '我的订单', icon: 'ProfileOutlined', component: './ProductOrder' },

  { path: '/analysis',access: 'canAdmin', name: '运行分析', icon: 'LineChartOutlined', component: './Admin/analysis/index.tsx' },

  {
    path: '/ProductOrder/info/:id',
    name: '查看订单详情',
    component: './ProductOrder/info',
    hideInMenu: true,
  },

  {
    path: '/account/center',
    name: '个人中心',
    icon: 'UserOutlined',
    component: './User/info',
    hideInMenu: true,
  },
  {
    path: '/user',
    layout: false,
    routes: [
      { name: '登录', path: '/user/login', component: './User/Login' },
      { name: '注册', path: '/user/register', component: './User/Register' },
      { component: './404' },
    ],
  },
  { path: '/', redirect: '/welcome' },

  { path: '*', layout: false, component: './404' },
];
