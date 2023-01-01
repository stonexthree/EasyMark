import {createApp, render} from 'vue'
//import './style.css'
import App from './App.vue'
import {createRouter, createWebHistory} from "vue-router";
import DocList from "./components/DocList.vue";
import Milkdown from "./components/Milkdown.vue";
import MemberDoc from "./components/MemberDoc.vue";
import MyDoc from "./components/MyDoc.vue";
import Test from "./components/Test.vue";
import ShowDoc from './components/ShowDoc.vue'
import AccountManagement from './components/AccountManagement.vue'
import AccountProfile from './components/AccountProfile.vue'
import {searchStatus,loginStatus} from './globalStatus'
import axios from 'axios'
import {DocApi} from './api-define'

//控制缩放
function bodyScale() {
    const deviceWidth = document.documentElement.clientWidth;
    const scale =  (1440 *0.8)/deviceWidth;
    (<any>document.body.style).zoom = scale;
}
window.onload = window.onresize = function () {
    bodyScale();
};

axios.interceptors.response.use(function (response) {
    if(response.data.code === 'A0200'){
        loginStatus.loginFailed();
    }
    return response;
}, function (error) {
    console.log(error);
    return Promise.reject(error);
});

const webRoute = [
    {
        name: 'docList',
        path: "/doc-list", component: DocList, props: {
            widthPercent: 80,
            docsApiConfig:DocApi.getAllDoc()
        }
    },
    {
        name: 'memberDoc',
        path: "/member-doc", component: MemberDoc, props: {
            widthPercent: 80
        }
    },
    {
        name: 'myDoc',
        path: "/my-doc", component: MyDoc, props: {
            loading: false,
            widthPercent: 80
        }
    },
    {
        name:'newDoc',
        path: "/new-doc", component: Milkdown
    },
    {
        name:'showDoc',
        path: '/doc/:docId', component: ShowDoc, props: {
            readOnly: true,
        }
    },
    {
        name: 'editDoc',
        path: '/edit-doc/:docId', component: Milkdown, props: {
            preload: true,
        }
    },
    {
        name: 'searchResult',
        path: "/result", component: DocList,
        props(){
            return {
                docsApiConfig:searchStatus.value.searchApi,
                loading: false,
                widthPercent: 80
            }
        }
    },
    {
        name: 'accountManagement',
        path: '/account-management',
        component: AccountManagement
    },
    {
        name:'accountProfile',
        path:'/profile',
        component: AccountProfile
    }
    ,
    {
        name: 'test',
        path: '/test',component: Test
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes: webRoute
})
const vue = createApp(App);
vue.use(router).mount('#app');
export {vue,webRoute};
