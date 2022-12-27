<template>
  <n-form
      ref="formRef"
      :label-width="150"
      :rules="rules"
      size="large"
      label-placement="left"
      label-align="left"
      :model="newAccountInfo"
  >
    <n-form-item label="密码" path="password">
      <n-input v-model:value="newAccountInfo.password" placeholder="输入密码" type="password"/>
    </n-form-item>
    <n-form-item ref="rPasswordRef" label="再次输入密码" path="reenteredPassword">
      <n-input v-model:value="newAccountInfo.reenteredPassword" placeholder="输入密码" type="password"/>
    </n-form-item>
    <n-form-item>
      <n-button attr-type="button" @click="setAccountPassword">
        确定
      </n-button>
    </n-form-item>
  </n-form>
</template>

<script lang="ts">
export default {
  name: "SetPasswordModal"
}
</script>
<script setup lang="ts">
import {h, defineComponent, Ref, ref, computed, onMounted} from 'vue'
import {
  NButton, NIcon, useMessage, NDataTable, NForm, NFormItem, NInput, NModal,
  FormRules, FormItemRule, FormItemInst, FormInst,
  useNotification,
} from 'naive-ui'
import axios from "axios";
import {AxiosRequestConfig} from 'axios'
import {UserApi} from "../../api-define";

const emits = defineEmits(['submitted']);
const notification = useNotification();
const props = defineProps({
  //不提供accountName的情况下默认走普通用户api去修改自己的密码，提供的情况下走管理员api修改指定账户的密码
  accountName: String,
})

const newAccountInfo: Ref<{
  password: string,
  reenteredPassword: string
}> = ref({
  password: '',
  reenteredPassword: ''
})
const rPasswordRef: Ref<FormItemInst | null> = ref(null);
const formRef = ref<FormInst | null>(null);
const formValidate: Ref<{ password: boolean, rpassword: boolean, validateResult: Function }> = ref({
  password: false, rpassword: false,
  validateResult: function () {
    return this.password && this.rpassword;
  }
});
const rules: FormRules = {
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

function setAccountPassword() {
  if (!formValidate.value.validateResult()) {
    notification.create({
      title: '验证未通过',
      duration: 2500,
      closable: false
    })
    return;
  }
  const apiConfig:AxiosRequestConfig = props.accountName === undefined ?
      UserApi.changePassword(newAccountInfo.value.password) :
      UserApi.setPassword(<string>props.accountName, newAccountInfo.value.password);
  axios.request(apiConfig).then((response) => {
    if (response.data.code === '00000') {
      notification.create({
        title: '修改成功',
        duration: 2500,
        closable: false
      })
      emits('submitted');
      return;
    }
    notification.create({
      title: '失败',
      content: response.data.code + ':' + response.data.message,
      duration: 2500,
      closable: false
    })
    emits('submitted');
  }).catch((response) => {
    emits('submitted');
    console.log(response);
  });
}

</script>
<style scoped>

</style>