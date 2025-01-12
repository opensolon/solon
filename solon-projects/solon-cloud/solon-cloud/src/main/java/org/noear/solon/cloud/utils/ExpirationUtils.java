/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.cloud.utils;

/**
 * @author noear
 * @since 1.3.1
 */
public class ExpirationUtils {
    public static long getExpiration(int times){
        int second = 0;

        switch (times){
            case 0:second  = 0;break;//0秒
            case 1:second  = second+5;break; //5秒
            case 2:second  = second+10;break; //10秒
            case 3:second  = second+30;break; //30秒
            case 4:second  = second+60;break; //1分钟
            case 5:second  = second+2*60;break;//2分种
            case 6:second  = second+5*60;break;//5分钟
            case 7:second  = second+10*60;break;//10分钟
            case 8:second  = second+30*60;break;//30分钟
            case 9:second  = second+60*60;break;//1小时
            default:second = second+120*60;break;//2小时
        }

        return second * 1000;
    }
}
