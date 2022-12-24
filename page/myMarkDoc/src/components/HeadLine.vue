<template>
  <SearchTool />
  <div class="head-line-container">
    <div class="logo">MyMarkDoc</div>
    <div class="search-div">
      <div class="circle-border left-border"></div>
      <div class="circle-border right-border"></div>
      <n-icon id="search-icon" color="rgba(220,220,220,1)" size="24" class="search-div-icon">
        <Search/>
      </n-icon>
      <span class="search-text vertical-center">Search</span>
      <n-icon id="key-command" color="rgba(220,220,220,1)" size="20" class="search-div-icon">
        <Command/>
      </n-icon>
      <n-icon id="key-k" color="rgba(220,220,220,1)" size="20" class="search-div-icon">
        <LetterK/>
      </n-icon>
    </div>
    <div class="user-profile">
      <div class="slide-div">
        <div class="name-div">
          <n-icon size="32" color="#1E92A0FF" class="user-icon">
            <UserOutlined/>
          </n-icon>
          <div class="user-nickname">{{loginStatus.standardProfile.nickname}}</div>
        </div>
        <div class="account-action-div">
          <n-icon size="32" color="#1E92A0FF" @click="router.push({name:'accountProfile'})" style="left: 32px" class="profile-icon account-action">
            <UserProfile/>
          </n-icon>
          <n-icon size="32" color="#1E92A0FF" @click="showSetPasswordModal=true" style="left: 64px" class="password-icon account-action">
            <Password/>
          </n-icon>
          <n-icon size="32" color="#1E92A0FF" @click="clickLogout" style="left: 96px" class="logout-icon account-action">
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
        z-index=2
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
import {Password, Logout, Document, List, DocumentAdd, StarReview,UserProfile} from '@vicons/carbon'
import {onMounted,ref,Ref} from 'vue'
import {useRouter} from 'vue-router'
import {loginStatus} from '../globalStatus'
import {UserApi} from "../api-define";
import axios from 'axios'
import SearchTool from './SearchTool.vue'
import SetPasswordForm from './forms/SetPasswordForm.vue'

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

.logo {
  position: absolute;
  height: 100%;
  width: 300px;
  left: 48px;
  color: #ffffff;
  font-size: 3em;
  user-select: none;
}

.search-div {
  height: 32px;
  width: 150px;
  background-color: #191C21FF;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 290px;
}

#search-icon {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
}

#key-command {
  left: 100px;
  border-style: solid;
  border-width: 0px;
  border-radius: 20%;
  position: absolute;
  top: 50%;
  background-color: #32435e;
  transform: translate(0%, -50%);
}

#key-k {
  left: 125px;
  border-style: solid;
  border-width: 0px;
  border-radius: 20%;
  position: absolute;
  top: 50%;
  background-color: #32435e;
  transform: translate(0%, -50%);
}

.circle-border {
  position: absolute;
  height: 32px;
  width: 32px;
  border-radius: 50%;
  background-color: #191C21FF;
  transform: translateX(-50%);
}

.right-border {
  left: 100%;
}

.search-text {
  display: inline-block;
  font-size: 1.5em;
  position: absolute;
  left: 20px;
  color: rgba(120, 120, 120, 1);
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