<template>
  <div id="component-root" class="window">
    <div id="action-button-bar">
      <n-button class="button" icon-placement="left" secondary strong @click="page=1">
        <template #icon>
          <n-icon>
            <Users/>
          </n-icon>
        </template>
        浏览用户
      </n-button>
      <n-button class="button" icon-placement="left" secondary strong @click="page=2">
        <template #icon>
          <n-icon>
            <UserFollow/>
          </n-icon>
        </template>
        创建账户
      </n-button>
    </div>
    <div id="container" :class="moveComputed">
      <n-data-table id="account-table"
                    :columns="columns"
                    :data="accountData"
                    :pagination=false
                    :bordered="true"
                    :single-line="true"
                    :single-column="true"
                    size="large"
                    flex-height
                    striped
      />
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
        <SetPasswordForm :account-name="setPasswordAccountName" @submitted="onFormSubmitted"/>
      </n-modal>
      <div id="create-account">
        <div id="form-area">
          <n-form
              ref="formRef"
              :label-width="150"
              :rules="rules"
              size="large"
              label-placement="left"
              label-align="left"
              :model="newAccountInfo"
          >
            <n-form-item label="用户名" path="loginName">
              <n-input v-model:value="newAccountInfo.loginName" placeholder="输入用户名"/>
            </n-form-item>
            <n-form-item label="密码" path="password">
              <n-input v-model:value="newAccountInfo.password" placeholder="输入密码" type="password"/>
            </n-form-item>
            <n-form-item ref="rPasswordRef" label="再次输入密码" path="reenteredPassword">
              <n-input v-model:value="newAccountInfo.reenteredPassword" placeholder="输入密码" type="password"/>
            </n-form-item>
            <n-form-item>
              <n-button attr-type="button" @click="createAccount">
                创建账户！
              </n-button>
            </n-form-item>
          </n-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "accountManagement"
}
</script>

<script setup lang="ts">
import {h, defineComponent, Ref, ref, computed, onMounted} from 'vue'
import {
  NButton, NIcon, useMessage, NDataTable, NForm, NFormItem, NInput, NModal,
  FormRules, FormItemRule, FormItemInst, FormInst,
  useDialog, useNotification
} from 'naive-ui'
import type {DataTableColumns} from 'naive-ui'
import {UserFollow} from '@vicons/carbon'
import {Users} from '@vicons/tabler'
import {UserApi} from '../api-define'
import axios from 'axios'
import SetPasswordForm from './sub/SetPasswordForm.vue'

const notification = useNotification();

//////////////
//功能切换部分

const page: Ref<number> = ref(0);
const moveComputed = computed(() => {
  if (page.value === 0) {
    return '';
  }
  if (page.value === 1) {
    return 'moveReverse';
  }
  if (page.value === 2) {
    return 'move';
  }
})

//////////////
//账户展示部分

interface Account {
  LoginName: string;
  Nickname: string;
  CreateTime: number
}

interface AccountData extends Account {
  key: string
}

const columns = [
  {
    title: '用户名',
    key: 'LoginName'
  },
  {
    title: '昵称',
    key: 'Nickname'
  },
  {
    title: '创建时间',
    key: 'CreateTime',
    sorter: (row1: Account, row2: Account) => row1.CreateTime - row2.CreateTime,
    render(row: Account) {
      return h(
          'span',
          {
            innerHTML: new Date(row.CreateTime).toLocaleDateString()
          }
      )
    },
  },
  {
    title: '操作',
    key: 'Action',
    render(row: Account) {
      return h(
          NButton,
          {
            strong: true,
            tertiary: true,
            size: 'small',
            onClick: () => callSetPasswordModal(row.LoginName)
          },
          {default: () => '重置密码'}
      )
    }
  }
]
const accountData: Ref<AccountData[]> = ref([]);
const setPasswordAccountName: Ref<string> = ref('');
const showSetPasswordModal: Ref<boolean> = ref(false);

function callSetPasswordModal(name: string) {
  showSetPasswordModal.value = true;
  setPasswordAccountName.value = name;
}

function onFormSubmitted() {
  showSetPasswordModal.value = false;
  setPasswordAccountName.value = '';
}

function mockData(size: number): AccountData[] {
  const result: AccountData[] = [];
  for (let i: number = 0; i < size; i++) {
    let t: AccountData = {
      key: i + '',
      LoginName: 'account' + i,
      Nickname: '账户' + i,
      CreateTime: 1671516417855 + i * 10000000
    }
    result.push(t);
  }
  return result;
}

//////////////////////
//创建用户部分

const newAccountInfo: Ref<{
  loginName: string,
  password: string,
  reenteredPassword: string,
  clear: Function
}> = ref({
  loginName: '',
  password: '',
  reenteredPassword: '',
  clear: () => {
    newAccountInfo.value.loginName = '';
    newAccountInfo.value.password = '';
    newAccountInfo.value.reenteredPassword = '';
  }
})
const rPasswordRef: Ref<FormItemInst | null> = ref(null);
const formRef = ref<FormInst | null>(null);
const formValidate: Ref<{ name: boolean, password: boolean, rpassword: boolean, validateResult: Function }> = ref({
  name: false, password: false, rpassword: false,
  validateResult: function () {
    console.log(this.name + '\n' + this.password + '\n' + this.rpassword)
    return this.name && this.password && this.rpassword;
  }
});
const rules: FormRules = {
  loginName: [
    {
      required: true,
      validator(rule: FormItemRule, value: string) {
        formValidate.value.name = false;
        if (value === '') {
          return new Error('需要输入用户名')
        }
        if (value.length < 4 || value.length > 16) {
          return new Error('用户名长度在4-16位之间')
        }
        if (!/^[0-9a-zA-Z_@#-]+$/.test(value)) {
          return new Error('用户名只允许以下几种：数字、字母、字符：_@#-')
        }
        formValidate.value.name = true;
        return true
      },
      trigger: ['input', 'blur', 'focus']
    }
  ],
  password: [
    {
      required: true,
      validator(rule: FormItemRule, value: string) {
        formValidate.value.password = false;
        rPasswordRef.value?.validate({trigger: 'password-input'})
        if (value === '') {
          return new Error('需要输入密码')
        }
        if (value.length < 8 || value.length > 16) {
          return new Error('密码长度需在8-16位之间')
        }
        if (!/^[0-9a-zA-Z!@#$%^&*-_=+]+$/.test(value)) {
          return new Error('密码只允许以下几种：数字、字母、字符：!@#$%^&*-_=+')
        }
        formValidate.value.password = true;
        return true
      },
      trigger: ['input', 'blur', 'focus']
    }
  ],
  reenteredPassword: [
    {
      required: true,
      validator(rule: FormItemRule, value: string) {
        formValidate.value.rpassword = false;
        if (value === '') {
          return new Error('请再次输入密码');
        }
        if (value !== newAccountInfo.value.password) {
          return new Error('两次输入密码不一致');
        }
        formValidate.value.rpassword = true;
        return true
      },
      trigger: ['input', 'blur', 'focus', 'password-input']
    }
  ]
}

function createAccount() {
  if (!formValidate.value.validateResult()) {
    notification.create({
      title: '验证未通过',
      duration: 2500,
      closable: false
    })
    return;
  }
  axios.request(UserApi.createAccount(newAccountInfo.value.loginName, newAccountInfo.value.password)).then((response) => {
    if (response.data.code === '00000') {
      notification.create({
        title: '成功',
        duration: 2500,
        closable: false
      })
      newAccountInfo.value.clear();
      return;
    }
  }).catch((response) => {
    console.log(response);
    newAccountInfo.value.clear();
  });
}

//////////////////
onMounted(() => {
  //获取所有的用户信息
  axios.request(UserApi.getAllUsers()).then((response) => {
    if (response.data.code === '00000') {
      for (let i in response.data.data) {
        let data = response.data.data[i];
        let t: AccountData = {
          key: i,
          LoginName: data.userName,
          Nickname: data.userNickname,
          CreateTime: data.createTimestamp
        };
        accountData.value.push(t);
      }
    }
  }).catch((response) => console.log(response));
})
////////////////////
//样式部分
const zIndex:Ref<any>=ref(2);
</script>

<style scoped>
#component-root {
  position: absolute;
  height: calc(100% - 30px);
  width: 80%;
  left: 50%;
  transform: translateX(-50%);
}

.window {
  overflow: hidden;
}

#action-button-bar {
  height: 60px;
  width: 100%;
}

#action-button-bar .button {
  margin-right: 10px;
}

#container {
  position: relative;
  height: calc(100% - 60px);
  width: 200%;
  transform: translateX(0%);
  animation-fill-mode: both;
  animation-duration: 1s;
}

.move {
  animation-name: moveX;
}

.moveReverse {
  animation-name: moveXR;
}

@keyframes moveX {
  0% {
    transform: translateX(0%);
  }
  100% {
    transform: translateX(-50%);
  }
}

@keyframes moveXR {
  0% {
    transform: translateX(-50%);
  }
  100% {
    transform: translateX(0%);
  }
}

#account-table {
  position: absolute;
  left: 0px;
  width: 50%;
  height: 100%;
}

#create-account {
  position: absolute;
  left: 50%;
  height: 100%;
  width: 50%;
}

#form-area {
  position: absolute;
  width: 600px;
  background-color: transparent;
  top: 0px;
  padding: 0px;
}
</style>