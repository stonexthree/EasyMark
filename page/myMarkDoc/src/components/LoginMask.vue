<template>
  <n-config-provider :theme-overrides=themeOverride>
    <div id="mask-background" :class="isLogin ? 'mask-off':'mask-on'" :style="{zIndex:loginStatus.statusChanged?'10':'-10'}">
      <n-card class="center" :bordered="false" id="login-card" title="登录">
        <n-form ref="formRef" :model="model" :rules="rules">
          <n-form-item path="age" label="用户名">
            <n-input v-model:value="username"/>
          </n-form-item>
          <n-form-item path="password" label="密码">
            <n-input v-model:value="password"
                     type="password"
            />
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
  </n-config-provider>
</template>

<script lang="ts">

export default {
  name: 'LoginMask'
}
</script>

<script setup lang="ts">
import {NCard, NForm, NFormItem, NInput, NRow, NCol, NButton, NConfigProvider} from 'naive-ui';
import {ref, watch} from "vue";
import axios from 'axios'
import {UserApi} from '../api-define'
import {loginStatus} from "../globalStatus";

const themeOverride = {
  "Card": {
    "titleTextColor": "rgba(200,200,200,1)"
  },
  "Form": {
    "labelTextColor": "rgba(200,200,200,1)"
  }
}

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
        console.log(response.data)
        //const data = JSON.parse(response.data);
        if (response.data.code === '00000') {
          emit('loginSuccess', username.value)
        } else {
          console.log('failed')
        }
      }
  )
}
</script>

<style scoped>
#mask-background {
  background-color: #252a32;
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

.center {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}


#login-card {
  width: 300px;
  height: 400px;
  background-color: #2e3440;
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