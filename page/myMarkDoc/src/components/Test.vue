<template>
  <PictureUpload :extend-actions=extendAction />
</template>

<script lang="ts">

export default {
};
</script>

<script setup lang="ts">
import PictureUpload from './PictureUpload.vue'
import {ImageAction} from '../model/models'
import {useNotification} from 'naive-ui'

const notification = useNotification();
const extendAction:ImageAction[] = [{
  title: '在光标处插入图片',
  action: ()=>{
    notification.create({
      content: '成功插入图片',
      duration: 2000
    })
  }
}]

</script>

<style scoped>

</style>