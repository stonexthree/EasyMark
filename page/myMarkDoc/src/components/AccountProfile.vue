<template>
  <div id="component-root">
    <div class="data-line">
      <span class="item-name">用户名：</span><span class="item-value">{{loginStatus.standardProfile.name}}</span>
    </div>
    <div class="data-line">
      <span class="item-name">昵称：</span>
      <div id="nickname" >
        <n-input :status="nicknameCheck?'error':'success'" v-model:value="nickname" size="large" placeholder=""
        @keydown.enter="changeNickname"/>
      </div>
    </div>
    <div class="data-line">
      <span class="item-name">创建时间：</span><span class="item-value">{{loginStatus.standardProfile.createTime}}</span>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "AccountProfile"
}
</script>

<script setup lang="ts">
import {loginStatus} from '../globalStatus'
import {NInput,useNotification} from 'naive-ui'
import {ref,Ref,onMounted,computed} from 'vue'
import {UserApi} from '../api-define'
import axios from 'axios'
import {customComponentThemeProvider} from "../theme";

const notification = useNotification();

const nickname:Ref<string> = ref('');
nickname.value = loginStatus.standardProfile.nickname;

const nicknameCheck = computed<boolean>(()=>{
  return (/\s/.test(nickname.value)||nickname.value.length<2||nickname.value.length>16);
})


function changeNickname(){
  const target = nickname.value;
  if(nicknameCheck.value){
    notification.create({
      title:'失败',
      content:'昵称中不能有空白字符且长度在 2-16 之间',
      duration:3000
    })
    return;
  }
  axios.request(UserApi.changeNickname(target)).then((response)=>{
    if(response.data.code==='00000'){
      loginStatus.standardProfile.nickname=target;
      notification.create({
        title:'成功',
        duration:2500
      })
      return;
    }
    notification.create({
      title:'失败',
      content:response.data.code + ': '+ response.data.message,
      duration:3000
    })
  }).catch((response)=>console.log(response));
}

onMounted(()=>{
})

///////////////////////
//样式部分
const colorSet = computed(()=>{
  return customComponentThemeProvider.value.colorSet;
})
</script>

<style scoped>
#component-root{
  position: absolute;
  width: 80%;
  min-width: 120px;
  background-color: v-bind(colorSet.halfDeep);
  height: calc(100% - 30px);
  overflow-x:hidden;
  overflow-y: scroll;
  left: 50%;
  transform: translateX(-50%);
}
#component-root::-webkit-scrollbar{
  display: none;
}
.data-line{
  margin:40px 0px 20px 0px;
  position: relative;
  left: 80px;
  height: 40px;
  color: v-bind(colorSet.fontColor4);
}
.data-line .item-name{
  line-height: 100%;
  height: 100%;
  font-size: 32px;
  min-width: 200px;
  display: inline-block;
}
.data-line .item-value{
  line-height: 100%;
  height: 100%;
  font-size: 32px;
}
#nickname{
  height: 100%;
  top:0px;
  position: absolute;
  display: inline-block;
}
</style>