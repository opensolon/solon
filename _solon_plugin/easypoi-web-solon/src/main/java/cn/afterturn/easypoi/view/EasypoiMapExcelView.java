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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;

import cn.afterturn.easypoi.entity.vo.MapExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;

/**
 * Map 对象接口
 * 
 * @author JueYue
 *  2014年11月25日 下午3:26:32
 */
@SuppressWarnings("unchecked")
@Component(MapExcelConstants.EASYPOI_MAP_EXCEL_VIEW)
public class EasypoiMapExcelView extends MiniAbstractExcelView {

    public EasypoiMapExcelView() {
        super();
    }

    @Override
    protected void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String codedFileName = "临时文件";
        Workbook workbook = ExcelExportUtil.exportExcel(
            (ExportParams) model.get(MapExcelConstants.PARAMS),
            (List<ExcelExportEntity>) model.get(MapExcelConstants.ENTITY_LIST),
            (Collection<? extends Map<?, ?>>) model.get(MapExcelConstants.MAP_LIST));
        if (model.containsKey(MapExcelConstants.FILE_NAME)) {
            codedFileName = (String) model.get(MapExcelConstants.FILE_NAME);
        }
        out(workbook, codedFileName, ctx);
    }

}
