<template>
  <div id="lowest-background"></div>
  <n-config-provider :theme="customComponentThemeProvider.naiveUITheme.perSet" :theme-overrides="customComponentThemeProvider.naiveUITheme.override">
    <n-notification-provider>
      <n-dialog-provider>
        <LoginMask :is-login=loginStatus.isLogin @login-success='loginStatus.loginSuccess()'/>
        <div id="app-content">
          <HeadLine id="headline"/>
        </div>
        <MiniOptionsPanel id="options-panel"/>
        <div id="main-div">
          <router-view :key="$route.fullPath"></router-view>
        </div>
        <div id="extend-message">
          <svg :style="githubStyle" @click="goGithub">
            <LogoGithub />
          </svg>
          <br/>
          0.2.2-beta | MIT Licence
        </div>
      </n-dialog-provider>
    </n-notification-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import {NConfigProvider,NButton,NIcon} from 'naive-ui';
import {loginStatus} from './globalStatus'
import {customComponentThemeProvider,ColorSet} from './theme.js'
import axios from "axios";
import {UserApi} from "./api-define";
import { onMounted,computed } from 'vue'
import {LogoGithub} from '@vicons/carbon'
import PictureUpload from './components/PictureUpload.vue'

function loadProfile(){
  axios.request(UserApi.getMyProfile()).then((response)=>{
    if(response.data.code === '00000'){
      loginStatus.standardProfile.name = response.data.data.userName;
      loginStatus.standardProfile.nickname = response.data.data.userNickname;
      loginStatus.standardProfile.createTime = new Date(response.data.data.createTimestamp).toLocaleDateString();
    }
  }).catch((response)=>console.log(response))
}

onMounted(()=>{
  loadProfile();
  loginStatus.registerAction(loadProfile,true);
})

const colorSet = computed<ColorSet>(()=>{
  return customComponentThemeProvider.value.colorSet;
})
const githubStyle = computed(()=>{
  return {
    height:'24px',
    width: '24px',
  }
})
function goGithub(){
  window.open("https://github.com/stonexthree/EasyMark","_blank");
}

</script>

<script lang="ts">
import DocList from "./components/DocList.vue"
import HeadLine from "./components/HeadLine.vue"
import LoginMask from "./components/LoginMask.vue"
import MemberDoc from "./components/MemberDoc.vue"
import MilkDown from './components/Milkdown.vue'
import MiniOptionsPanel from './components/MiniOptionsPanel.vue'
import SearchTool from "./components/SearchTool.vue";
import MyDoc from './MyDoc.vue'
import ShowDoc from './components/ShowDoc.vue'
import Test from "./components/Test.vue"
import AccountProfile from "./components/AccountProfile.vue"
import {NDialogProvider, NNotificationProvider} from 'naive-ui'

export default {
  name: "App",
};
</script>
<style>
#login-mask-top-div {
  width: 100%;
  height: 100%;
  z-index: 10;
  position: absolute;
}

#app-content {
  width: 100%;
  height: 100%;
  position: absolute;
  background-color: v-bind(colorSet.deep);
}

#headline {
  position: fixed;
  top: 0px;
}

#side-options {
  position: fixed;
}

#options-panel {
  position: fixed;
}

#extend-message{
  position: fixed;
  bottom: 20px;
  left: 48px;
  color: v-bind(colorSet.fontColor4);
}

#main-div {
  position: absolute;
  top: 100px;
  height: calc(100% - 100px);
  width: calc(100% - 200px);
  left: 200px;
  display: flex;
  flex-flow: row;
  overflow-y: scroll;
  overflow-x: hidden;
}
#main-div::-webkit-scrollbar{
  display: none;
}

svg:hover{
 cursor: pointer;
}

#lowest-background{
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: v-bind(colorSet.deep);
}
</style>