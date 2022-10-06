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

import cn.afterturn.easypoi.entity.vo.BigExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import org.apache.poi.ss.usermodel.Workbook;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

import java.util.Map;

/**
 * @author JueYue on 14-3-8. Excel 生成解析器,减少用户操作
 */
@Component(BigExcelConstants.EASYPOI_BIG_EXCEL_VIEW)
public class EasypoiBigExcelExportView extends MiniAbstractExcelView {

    public EasypoiBigExcelExportView() {
        super();
    }

    @Override
    protected void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String codedFileName = "临时文件";
        Workbook workbook = ExcelExportUtil.exportBigExcel(
                (ExportParams) model.get(BigExcelConstants.PARAMS),
                (Class<?>) model.get(BigExcelConstants.CLASS),
                (IExcelExportServer) model.get(BigExcelConstants.DATA_INTER),
                model.get(BigExcelConstants.DATA_PARAMS));
        if (model.containsKey(BigExcelConstants.FILE_NAME)) {
            codedFileName = (String) model.get(BigExcelConstants.FILE_NAME);
        }
        out(workbook, codedFileName, ctx);
    }
}
