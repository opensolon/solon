/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.view;

import cn.afterturn.easypoi.util.WebFilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.noear.solon.core.handle.Context;

import java.io.OutputStream;

/**
 * 基础抽象Excel View
 * @author JueYue
 *  2015年2月28日 下午1:41:05
 */
public abstract class MiniAbstractExcelView extends PoiBaseView {

    private static final String CONTENT_TYPE = "text/html;application/vnd.ms-excel";

    protected static final String HSSF = ".xls";
    protected static final String XSSF = ".xlsx";

    public MiniAbstractExcelView() {
        setContentType(CONTENT_TYPE);
    }


    public void out(Workbook workbook, String codedFileName, Context ctx) throws Exception {
        if (workbook instanceof HSSFWorkbook) {
            codedFileName += HSSF;
        } else {
            codedFileName += XSSF;
        }
        // 用工具类生成符合RFC 5987标准的文件名header, 去掉UA判断
        ctx.headerSet("content-disposition", WebFilenameUtils.disposition(codedFileName));
        OutputStream out = ctx.outputStream();
        workbook.write(out);
        out.flush();
    }

}
