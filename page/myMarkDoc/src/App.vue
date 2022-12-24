<template>
  <n-config-provider :theme="polarNightTheme.perSet" :theme-overrides="polarNightTheme.override">
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
      </n-dialog-provider>
    </n-notification-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import {NConfigProvider} from 'naive-ui';
import {loginStatus} from './globalStatus'
import {polarNightTheme} from './theme.js'
import axios from "axios";
import {UserApi} from "./api-define";
import { onMounted } from 'vue'
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
}

#headline {
  position: fixed;
  top: 0px;
}

#app-content {
  background-color: rgba(37, 41, 50, 1);
}

#side-options {
  position: fixed;
}

#options-panel {
  position: fixed;
}

#main-div {
  position: absolute;
  top: 80px;
  height: calc(100% - 80px);
  width: calc(100% - 200px);
  left: 200px;
  display: flex;
  flex-flow: row;
  overflow-y: scroll;
  overflow-x: hidden;
}

body {
  background-color: rgba(37, 41, 50, 1);
}
</style>