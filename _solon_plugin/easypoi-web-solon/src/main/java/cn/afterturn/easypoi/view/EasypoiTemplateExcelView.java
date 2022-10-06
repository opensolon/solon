/**
 * Copyright 2013-2015 JueYue (qrb.jueyue@gmail.com)
 *   
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.afterturn.easypoi.view;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.entity.vo.TemplateExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;

/**
 * Excel模板视图
 * 
 * @author JueYue
 *  2014年6月30日 下午9:15:49
 */
@SuppressWarnings("unchecked")
@Component(TemplateExcelConstants.EASYPOI_TEMPLATE_EXCEL_VIEW)
public class EasypoiTemplateExcelView extends MiniAbstractExcelView {

    public EasypoiTemplateExcelView() {
        super();
    }

    @Override
    protected void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String codedFileName = "临时文件";
        @SuppressWarnings("deprecation")
        Workbook workbook = ExcelExportUtil.exportExcel(
            (TemplateExportParams) model.get(TemplateExcelConstants.PARAMS),
            (Class<?>) model.get(TemplateExcelConstants.CLASS),
            (List<?>) model.get(TemplateExcelConstants.LIST_DATA),
            (Map<String, Object>) model.get(TemplateExcelConstants.MAP_DATA));
        if (model.containsKey(NormalExcelConstants.FILE_NAME)) {
            codedFileName = (String) model.get(NormalExcelConstants.FILE_NAME);
        }
        out(workbook, codedFileName, ctx);
    }
}
