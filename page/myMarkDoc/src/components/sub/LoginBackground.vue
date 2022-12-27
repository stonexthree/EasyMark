<template>
  <div class="component-root">
    <div class="logo-content">
            <n-icon class="icon" color="white" size="200">
              <DrawFilled/>
            </n-icon>
            <div class="text">
              EASY MARK<br/>
              <div style="font-size: 30px"> 轻量级在线MarkDown编辑器</div>
            </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  name: "CircleBackground"
}
</script>
<script setup lang="ts">
import {ref, Ref, onMounted} from 'vue'
import {DrawFilled} from '@vicons/material'
import {NIcon} from 'naive-ui'


</script>

<style scoped>
.icon {
  position: absolute;
  left: 0px;
  top: 0px
}

.logo-content {
  position: absolute;
  background-color: transparent;
  height: 400px;
  width: 800px;
  top: 50%;
  transform: translateY(-50%);
  left: 20%;
}

.text {
  position: absolute;
  left: 250px;
  top: 0px;
  font-size: 100px;
  color: white
}
</style>