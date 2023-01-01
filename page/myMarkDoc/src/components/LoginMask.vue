<template>
    <div id="mask-background" :class="isLogin ? 'mask-off':'mask-on'" :style="{zIndex:loginStatus.statusChanged?10:-10}">
      <LoginBackground class="background"/>
      <div class="logo-content">
        <n-icon class="icon" :color="colorSet.fontColor" size="200">
          <DrawFilled/>
        </n-icon>
        <div class="text">
          EASY MARK<br/>
          <div style="font-size: 30px"> 轻量级在线MarkDown编辑器</div>
        </div>
      </div>
      <n-card :bordered="false" id="login-card" title="登录">
        <n-form ref="formRef">
          <n-form-item path="age" label="用户名">
            <n-input v-model:value="username" @keydown.enter="keyDown"/>
          </n-form-item>
          <n-form-item path="password" label="密码">
            <n-input v-model:value="password" type="password" @keydown.enter="keyDown"/>
          </n-form-item>
          <n-row :gutter="[0, 24]">
            <n-col :span="24">
              <div style="display: flex; justify-content: flex-end">
                <n-button
                    round
                    type="primary"
                    @click=clickLogin
                >
                  登录
                </n-button>
              </div>
            </n-col>
          </n-row>
        </n-form>
      </n-card>
    </div>
</template>

<script lang="ts">

export default {
  name: 'LoginMask'
}
</script>

<script setup lang="ts">
import {NCard, NForm, NFormItem, NInput, NRow, NCol, NButton, NConfigProvider,NIcon,useNotification} from 'naive-ui';
import {ref, watch,computed} from "vue";
import axios from 'axios'
import {UserApi} from '../api-define'
import {loginStatus} from "../globalStatus";
import {customComponentThemeProvider,ColorSet} from "../theme";
import LoginBackground from "./sub/LoginBackground.vue";
import {DrawFilled} from '@vicons/material'

const notification = useNotification();

const props = defineProps({
  'isLogin': Boolean
})

const emit = defineEmits([
  'loginSuccess'
])

const username = ref('')
const password = ref('')



function clickLogin(): void {
  const usernameTemp = username.value;
  const passwordTemp = password.value;
  username.value = '';
  password.value = '';
  axios(UserApi.login(usernameTemp, passwordTemp)).then(
      function (response) {
        //console.log(response.data)
        //const data = JSON.parse(response.data);
        if (response.data.code === '00000') {
          emit('loginSuccess', username.value)
          return;
        }
        notification.create({
          content:'账号密码错误',
          duration:2500
        })
      }
  ).catch(response => console.log(response))
}
function keyDown():void {
  if(username.value===''||password.value===''){
    notification.create({
      content:'请输入账号和密码',
      duration:2000
    })
    return;
  }
  clickLogin();
}

const colorSet = computed<any>(()=>{
  return customComponentThemeProvider.value.colorSet;
})
</script>

<style scoped>
#mask-background {
  background-color: v-bind(colorSet.deep);
  width: 100%;
  height: 100%;
  position: fixed;
  animation-duration: 1s;
  animation-fill-mode: both;
  animation-iteration-count: 1;
}

.mask-on {
  animation-name: mask-on;
}

.mask-off {
  animation-name: mask-off;
}

.background{
  position: absolute;
  width: 100%;
  height: 100%;
}

#login-card {
  position: absolute;
  width: 300px;
  height: 320px;
  background-color: v-bind(colorSet.halfDeep);
  top: 50%;
  right: 5%;
  transform: translate(0%,-70%);
}

.icon {
  position: absolute;
  left: 0px;
  top: 0px
}

.logo-content {
  position: absolute;
  background-color: transparent;
  height: 200px;
  width: 850px;
  top: 50%;
  transform: translate(0%,-100%);
  left: 10%;
}

.text {
  position: absolute;
  left: 250px;
  top: 0px;
  font-size: 100px;
  color: v-bind(colorSet.fontColor);
}

@keyframes mask-on {
  0% {
    transform: translateY(-100%);
  }
  100% {
    transform: translateY(0%);
  }
}

@keyframes mask-off {
  0% {
    transform: translateY(0%);
  }
  100% {
    transform: translateY(-100%);
  }
}

</style>