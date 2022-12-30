<template>
  <SearchTool />
  <div class="head-line-container">
    <div class="logo">
      <n-icon size="64" :color="colorSet.fontColor" class="img-logo" >
        <DrawFilled />
      </n-icon>
      E.M.</div>
    <n-icon class="theme-change" size="24" @click="ThemeStatus.next" :color="colorSet.extension1">
      <Sun v-show="ThemeStatus.currentTheme==='NordSnowStorm'"/>
      <Moon v-show="ThemeStatus.currentTheme==='NordPolarNight'"/>
    </n-icon>
    <div class="search-div">
      <div class="circle-border" id="left-border"></div>
      <div class="circle-border" id="right-border"></div>
      <n-icon id="search-icon" :color="colorSet.fontColor2" size="24" class="search-div-icon">
        <Search/>
      </n-icon>
      <span class="search-text vertical-center">Search</span>
      <n-icon id="key-command" :color="colorSet.fontColor2" size="20" class="search-div-icon">
        <Command/>
      </n-icon>
      <n-icon id="key-k" :color="colorSet.fontColor2" size="20" class="search-div-icon">
        <LetterK/>
      </n-icon>
    </div>
    <div class="user-profile">
      <div class="slide-div">
        <div class="name-div">
          <n-icon size="32" :color=iconColor class="user-icon">
            <UserOutlined/>
          </n-icon>
          <div class="user-nickname">{{loginStatus.standardProfile.nickname}}</div>
        </div>
        <div class="account-action-div">
          <n-icon size="32" :color=iconColor
                  @click="router.push({name:'accountProfile'})" style="left: 32px" class="profile-icon account-action">
            <UserProfile/>
          </n-icon>
          <n-icon size="32" :color=iconColor
                  @click="showSetPasswordModal=true" style="left: 64px" class="password-icon account-action">
            <Password/>
          </n-icon>
          <n-icon size="32" :color=iconColor
                  @click="clickLogout" style="left: 96px" class="logout-icon account-action">
            <logout/>
          </n-icon>
        </div>
      </div>
    </div>
    <n-modal
        v-model:show="showSetPasswordModal"
        class="custom-card"
        preset="card"
        :style="{width: '600px',position:'fixed',  top:'50%',left: '50%',transform: 'translate(-50%,-50%)'}"
        title="重置密码"
        size="huge"
        :bordered="false"
        :z-index=zIndex
        :mask-closable="false"
    >
      <SetPasswordForm @submitted="onFormSubmitted"/>
    </n-modal>
  </div>
</template>

<script lang="ts">
export default {
  name: "HeadLine"
}
</script>

<script setup lang="ts">
import {NIcon,NModal} from 'naive-ui'
import {UserOutlined} from '@vicons/antd'
import {Command, LetterK, Search} from '@vicons/tabler'
import {DrawFilled} from '@vicons/material'
import {Password, Logout, Document, List, DocumentAdd, StarReview,UserProfile,Sun,Moon} from '@vicons/carbon'
import {onMounted,ref,Ref,computed} from 'vue'
import {useRouter} from 'vue-router'
import {loginStatus} from '../globalStatus'
import {UserApi} from "../api-define";
import axios from 'axios'
import SearchTool from './SearchTool.vue'
import SetPasswordForm from './sub/SetPasswordForm.vue'
import {customComponentThemeProvider,ThemeName,ThemeStatus} from '../theme'


const router = useRouter();

/*const nickname:Ref<string> = ref('未指定昵称');*/

const showSetPasswordModal:Ref<boolean> = ref(false);
function onFormSubmitted(){
  showSetPasswordModal.value=false;
}

/*function loadNickname(){
  axios.request(UserApi.getLoginUserNickname()).then(
      (response) => {
        if(response.data.code === '00000'){
          nickname.value = response.data.data;
          return;
        }
        if(response.data.code === 'A0200'){
          //loginStatus.loginFailed();
          return;
        }
        console.log(response.data);
      }
  ).catch((response)=>console.log(response))
}*/

function clickLogout(){
  console.log('call logout');
  axios.request(UserApi.logout()).then((response) =>{
    if(response.data.code === '00000'){
      loginStatus.loginFailed();
    }
  }).catch((response)=>console.log(response));
}

onMounted(()=>{
  /*loadNickname();
  loginStatus.registerAction(loadNickname,true);//注册登录成功后的回调*/
})

//////////////////////
//样式部分
const iconColor = computed<any>(()=>{
  return customComponentThemeProvider.value.colorSet.extension1;
});
const colorSet = computed<any>(()=>{
  return customComponentThemeProvider.value.colorSet;
})
const zIndex:Ref<any>=ref(2);

//ThemeStatus.value.change('NordSnowStorm');
</script>

<style scoped>
.vertical-center {
  top: 50%;
  transform: translateY(-50%);
}

.head-line-container {
  height: 80px;
  width: 100%;
  position: fixed;
  background-color: transparent;
  z-index: 1;
}
.img-logo{
  position: relative;
  top:50%;
  transform: translateY(-50%);
}
.logo {
  position: absolute;
  height: 100%;
  width: 400px;
  left: 48px;
  color: v-bind(colorSet.fontColor);
  font-size: 4em;
  user-select: none;
  line-height: 80px;
  font-style: italic;
}

.theme-change{
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 420px;

}

.search-div {
  height: 32px;
  width: 150px;
  background-color: v-bind(colorSet.extension2);
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 230px;
}

#search-icon {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
}

#key-command {
  left: 100px;
  border-style: solid;
  border-width: 0px;
  border-radius: 20%;
  position: absolute;
  top: 50%;
  background-color: v-bind(colorSet.halfDeep);
  transform: translate(0%, -50%);
}

#key-k {
  left: 125px;
  border-style: solid;
  border-width: 0px;
  border-radius: 20%;
  position: absolute;
  top: 50%;
  background-color: v-bind(colorSet.halfDeep);
  transform: translate(0%, -50%);
}

.circle-border {
  position: absolute;
  height: 32px;
  width: 32px;
  border-radius: 50%;
  background-color: v-bind(colorSet.extension2);
  transform: translateX(-50%);
}

#right-border {
  left: 100%;
}

#left-border {
  left: 0px;
}
.search-text {
  display: inline-block;
  font-size: 1.5em;
  position: absolute;
  left: 25px;
  color: v-bind(colorSet.fontColor4);
  user-select: none;
}


.user-profile {
  position: absolute;
  width: 200px;
  height: 32px;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  overflow: hidden;
}
.slide-div{
  height: 64px;
  width: 100%;
  top:0px;
  position: absolute;
  animation-name: hide-account-actions;
  animation-duration: 1s;
  animation-iteration-count: 1;
  animation-fill-mode: both;
}

.user-profile:hover .slide-div{
  animation-name: show-account-actions;
}

.name-div {
  height: 32px;
  width: 100%;
  position: absolute;
  top:0px;
}

.name-div > * {
  position: absolute;
  top: 50%;
}

.user-icon {
  left: 32px;
  transform: translate(0%, -50%);
  padding-right: 10px;
}

.user-nickname {
  left: 64px;
  transform: translate(0%, -50%);
  padding-left: 10px;
  user-select: none;
  color: rgba(160,160,160,1);
}

.account-action-div {
  height: 32px;
  width: 100%;
  position: absolute;
  top: 32px;
}

.account-action-div .account-action:hover{
  cursor: pointer;
}

@keyframes show-account-actions{
  0%{
    transform: translateY(0%);
  }
  100%{
    transform: translateY(-50%);
  }
}
@keyframes hide-account-actions{
  0%{
    transform: translateY(-50%);
  }
  100%{
    transform: translateY(0%);
  }
}


/*
.hidden-options .options{
  height: 32px;
  line-height: 32px;
  text-align: center;
  background-color: gray;
}
@keyframes options-show-animation {
  0%{
    max-height: 0px;
  }
  100%{
    max-height: 110px;
  }
}
@keyframes options-hide-animation {
  0%{
    max-height: 110px;
  }
  100%{
    max-height: 0px;
  }
}
.hidden-options{
  animation-name: options-hide-animation;
  animation-iteration-count: 1;
  animation-duration: 1s;
  animation-fill-mode: both;
  overflow-y: hidden;
}
.user-profile:hover > .hidden-options{
  animation-name: options-show-animation;
  animation-iteration-count: 1;
  animation-duration: 1s;
  animation-fill-mode: both;
  overflow-y: hidden;
}
*/

</style>