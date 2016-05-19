pinyin4j-multi-ext
========

Forked from https://github.com/obiteaaron/pinyin4j-multi



### 多音字识别 ###
在pinyin4j的基础上添加了多音字识别，带近一万个多音词，但是这远远不够，所以用户可设置外挂词库	

### 外挂多音词库 ###
用户配置的外挂词库会覆盖系统中相同词的读音,可用于纠错

配置方式很简单,只需要配置路径即可 
```
MultiPinyinConfig.multiPinyinPath="/Users/yiboliu/my_multi_pinyin.txt"
```

格式同系统的多音词库,如: 
```
吸血鬼日记 (xi1,xue4,gui3,ri4,ji4)
```

多音词排列组合,如: 
```
System.out.println(PinyinHelper.Multitone("重庆"));
{simple_spell=[zq][cq], full_spell=[zhongqing][chongqing], full_spell_tone=[zhong4qing4][chong2qing4]}

重庆(zhong4qing4,chong2qing4) 全拼带声调
重庆(zhongqing,chongqing)     全拼不带声调
重庆(zq,cq)                   首字母

```

中文数字转阿拉伯数字,如: 
```
System.out.println(PinyinHelper.Chinese2Arab("三百五十六万零三百"));
三百五十六万零三百  =》 3560300

```

（中文+中文数字+字母+阿拉伯数字）排序,如: 
```
我520我的家五十一  =》 wo300520wo3de5jia100051
我71我的家五十二   =》 wo300071wo3de5jia100052

System.out.println(PinyinHelper.getSortNameSpell("我71我的家五十二"));
System.out.println(PinyinHelper.getSortNameSpell("我520我的家五十一"));
        

排序后
wo300071wo3de5jia100052    我71我的家五十二
wo300520wo3de5jia100051    我520我的家五十一

```