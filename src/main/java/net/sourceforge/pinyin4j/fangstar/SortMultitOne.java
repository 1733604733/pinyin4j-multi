/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package net.sourceforge.pinyin4j.fangstar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.StringUtils;

/**
 * 排序字段、多音字排列组合或不排列组合处理
 * @author FengShangZe
 */
public class SortMultitOne {

    static int range = 10000;

    static HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
    static Map<Character, Integer> num_map = new HashMap<Character, Integer>() {
        {
            put('零', 0);
            put('一', 1);
            put('二', 2);
            put('三', 3);
            put('四', 4);
            put('五', 5);
            put('六', 6);
            put('七', 7);
            put('八', 8);
            put('九', 9);
        }
    };

    static Map<Character, Integer> unit_map = new HashMap<Character, Integer>() {
        {
            put('十', 10);
            put('百', 100);
            put('千', 1000);
            put('万', 10000);
            put('亿', 100000000);
        }
    };

    /**
     * 获取 str 的带声调的全拼、不带声调的全拼、首字母
     *
     * @param str
     * @return all_spell["full_spell_tone"] : 带声调的全拼 ； all_spell["full_spell"] :
     * 不带声调的全拼； all_spell["simple_spell"] : 拼音首字母
     */
    public static Map<String, String> getNameSpell(String str) {
        Map<String, String> all_spell = new HashMap<String, String>() {
            {
                put("full_spell_tone", "");
                put("full_spell", "");
                put("simple_spell", "");
            }
        };

        if (str.trim().equals("")) {
            return all_spell;
        }

        String full_spell_tone = "";
        String full_spell = "";
        String simple_spell = "";
        try {
            String regex = ";";
            String spell = PinyinHelper.toHanYuPinyinString(str, outputFormat, regex, true);
            String[] split_spell = spell.split(regex);
            int cnt = split_spell.length;
            for (int i = 0; i < cnt; i++) {
                String tmp = split_spell[i];

                full_spell_tone += tmp;
                full_spell += (tmp.substring(0, tmp.length() - 1));
                simple_spell += tmp.charAt(0);
            }
            all_spell.put("full_spell_tone", full_spell_tone);
            all_spell.put("full_spell", full_spell);
            all_spell.put("simple_spell", simple_spell);

        } catch (BadHanyuPinyinOutputFormatCombination e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return all_spell;
    }

    /**
     * 获取排序字段
     *
     * @param str
     * @return
     */
    public static String getSortNameSpell(String str) {

        String sort_spell = "";
        String china_arab = "";

        Map<Integer, Integer> map = getChineseNum(str);
        Map<Integer, Integer> arbe_map = getArbeNum(str);

        Map<Integer, Integer> tree_map = new TreeMap<Integer, Integer>();

        tree_map.putAll(map);
        tree_map.putAll(arbe_map);

        int Index = 0;
        int start = 0;
        int end = 0;

        for (Map.Entry<Integer, Integer> entry : tree_map.entrySet()) {
            start = entry.getKey();
            end = entry.getValue();

            String tmp = str.substring(start, end);

            if (StringUtils.isNumeric(tmp) && Integer.parseInt(tmp) < range) {
                sort_spell += getNameSpell(str.substring(Index, start)).get("full_spell_tone");
                tmp = StringUtils.leftPad(tmp, String.valueOf(range).length(), '0');
                sort_spell += tmp;
                Index = end;
                continue;
            }
            china_arab = Chinese2Arab(tmp);
            if (china_arab.equals("-1")) {
                sort_spell += getNameSpell(str.substring(Index, end)).get("full_spell_tone");
            } else {
                sort_spell += getNameSpell(str.substring(Index, start)).get("full_spell_tone");
                sort_spell += china_arab;
            }
            Index = end;
        }
        if (end < str.length()) {
            sort_spell += getNameSpell(str.substring(Index, str.length())).get("full_spell_tone");
        }
        return sort_spell;
    }

    /**
     * 获取阿拉伯数字在str中的起始和结束位置（str中可能会有多个阿拉伯数字的子串）
     *
     * @param str
     * @return map.getKey() : 开始位置；map.getValue() : 结束位置
     */
    public static Map<Integer, Integer> getArbeNum(String str) {

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        if (str == null) {
            return map;
        }

        int i = 0;
        int start = -1;
        int end = -1;

        int flag = 0;
        int cnt_str = str.length();
        for (; i < cnt_str; i++) {
            char tmp = str.charAt(i);
            switch (tmp) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (flag == 0) {
                        start = i;
                        flag = 1;
                    }
                    break;
                default:
                    if (flag == 1) {
                        end = i;
                        flag = 0;
                        map.put(start, end);
                    }
                    break;
            }
        }
        //字符串最后子串是全中文数字时
        if (i == cnt_str && flag == 1) {
            map.put(start, i);
        }
        return map;
    }

    /**
     * 获取大写数字在str中的起始和结束位置（str中可能会有多个大写数字的子串）
     *
     * @param str
     * @return map.getKey() : 开始位置；map.getValue() : 结束位置
     */
    public static Map<Integer, Integer> getChineseNum(String str) {

        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        if (str == null) {
            return map;
        }

        int i = 0;
        int start = -1;
        int end = -1;

        int flag = 0;
        int cnt_str = str.length();
        for (; i < cnt_str; i++) {
            char tmp = str.charAt(i);
            if (num_map.get(tmp) != null || unit_map.get(tmp) != null) {
                if (flag == 0) {
                    start = i;
                    flag = 1;
                }
            } else if (flag == 1) {
                end = i;
                flag = 0;
                map.put(start, end);
            }
        }
        //字符串最后子串是全中文数字时
        if (i == cnt_str && flag == 1) {
            map.put(start, i);
        }
        return map;
    }

    /**
     * 中文数字转阿拉伯数字
     * @param str
     * @return -1 ： 中文数字不符合数字描述
     */
    public static String Chinese2Arab(String str) {
        int result = 0;
        if (str == null) {
            return "-1";
        }
        int cnt = str.length();

        int i = 0;
        int flag = 0;
        int tmp_result = 1;
        int cur_unit = 0;
        int pre_unit = 0;

        for (; i < cnt; i++) {
            char key = str.charAt(i);
            Object tmp = num_map.get(key);
            if (tmp == null) {//是单位
                tmp = unit_map.get(key);
                if (tmp == null) {
                    return "-1";
                }

                //连续两个单位就认定为非法数字
                if (flag == 2) {
                    return "-1";
                }

                cur_unit = Integer.parseInt(tmp.toString());

                if (cur_unit >= pre_unit) {
                    result = (result + tmp_result) * cur_unit;
                } else {
                    result += (tmp_result * cur_unit);
                }

                //上一个单位
                pre_unit = Integer.parseInt(tmp.toString());

                flag = 2;
            } else {//是数字
                if (Integer.parseInt(tmp.toString()) == 0) {
                    continue;
                }
                if (flag == 1) {
                    return "-1";
                }
                flag = 1;
                tmp_result = Integer.parseInt(tmp.toString());
            }
        }
        if (flag == 1) {
            result += tmp_result;
        }

        String sret = String.valueOf(result);
        if (result < range) {
            sret = StringUtils.leftPad(sret, String.valueOf(range).length(), "0");
        }

        return sret;
    }

    /**
     * 多音字进行排列组合
     *
     * @param str
     * @return Multitone("重庆"); {simple_spell=[zq][cq],
     * full_spell=[zhongqing][chongqing],
     * full_spell_tone=[zhong4qing4][chong2qing4]}
     */
    public static Map<String, String> Multitone(String str) {

        String full_spell_tone = "";
        String full_spell = "";
        String simple_spell = "";

        String tmp = "";

        Map<String, String> all_spell = new HashMap<String, String>();
        all_spell.put("full_spell_tone", full_spell_tone);
        all_spell.put("full_spell", full_spell);
        all_spell.put("simple_spell", simple_spell);

        if (str.trim().equals("")) {
            return all_spell;
        }

        int cnt = str.trim().length();
        if (cnt <= 0) {
            return all_spell;
        }

        List<String> full_spell_tone_list = new ArrayList<String>();
        List<String> full_spell_list = new ArrayList<String>();
        List<String> simple_spell_list = new ArrayList<String>();

        int i = 0, j = 0, x = 0;
        for (i = 0; i < cnt; i++) {
            if (0 == i) {
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
                for (j = 0; j < pinyin.length; j++) {

                    tmp = pinyin[j];
                    full_spell_tone_list.add(tmp);
                    full_spell_list.add(tmp.substring(0, tmp.length() - 1));
                    simple_spell_list.add(String.valueOf(tmp.charAt(0)));

                }
            }
            if (i > 0) {
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
                List<String> full_spell_tone_list_tmp = new ArrayList<String>();
                List<String> full_spell_list_tmp = new ArrayList<String>();
                List<String> simple_spell_list_tmp = new ArrayList<String>();

                for (j = 0; j < pinyin.length; j++) {
                    tmp = pinyin[j];
                    for (Iterator<String> iterator = full_spell_tone_list.iterator(); iterator
                            .hasNext();) {
                        full_spell_tone_list_tmp.add(iterator.next() + tmp);
                    }
                    for (Iterator<String> iterator = full_spell_list.iterator(); iterator.hasNext();) {
                        full_spell_list_tmp.add(iterator.next()
                                + tmp.substring(0, tmp.length() - 1));
                    }
                    for (Iterator<String> iterator = simple_spell_list.iterator(); iterator
                            .hasNext();) {
                        simple_spell_list_tmp.add(iterator.next() + String.valueOf(tmp.charAt(0)));
                    }
                }

                full_spell_tone_list = full_spell_tone_list_tmp;
                full_spell_list = full_spell_list_tmp;
                simple_spell_list = simple_spell_list_tmp;
            }
        }

        for (Iterator<String> iterator = full_spell_tone_list.iterator(); iterator.hasNext();) {
            full_spell_tone += ("[" + iterator.next() + "]");
        }
        for (Iterator<String> iterator = full_spell_list.iterator(); iterator.hasNext();) {
            full_spell += ("[" + iterator.next() + "]");
        }
        for (Iterator<String> iterator = simple_spell_list.iterator(); iterator.hasNext();) {
            simple_spell += ("[" + iterator.next() + "]");
        }

        all_spell.put("full_spell_tone", full_spell_tone);
        all_spell.put("full_spell", full_spell);
        all_spell.put("simple_spell", simple_spell);

        return all_spell;
    }
}
