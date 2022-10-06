/**
 * Copyright 2013-2015 xfworld (xfworld@gmail.com)
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

import cn.afterturn.easypoi.entity.vo.MapExcelGraphConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.graph.builder.ExcelChartBuildService;
import cn.afterturn.easypoi.excel.graph.entity.ExcelGraph;
import org.noear.solon.core.handle.Context;

/**
 * Map 对象接口
 * 
 * @author xfworld
 * @since  2016-01-04
 */
@SuppressWarnings("unchecked")
@Component(MapExcelGraphConstants.MAP_GRAPH_EXCEL_VIEW)
public class MapGraphExcelView extends MiniAbstractExcelView {
	
    public MapGraphExcelView() {
        super();
    }

    @Override
    protected void renderOutputModel(Map<String, Object> model, Context ctx) throws Exception {
        String codedFileName = "临时文件";
        ExportParams params=(ExportParams)model.get(MapExcelGraphConstants.PARAMS);
        List<ExcelExportEntity> entityList=(List<ExcelExportEntity>) model.get(MapExcelGraphConstants.ENTITY_LIST);
        List<Map<String, Object>> mapList=(List<Map<String, Object>>)model.get(MapExcelGraphConstants.MAP_LIST);
        List<ExcelGraph> graphDefinedList=(List<ExcelGraph>)model.get(MapExcelGraphConstants.GRAPH_DEFINED);
        //构建数据
        Workbook workbook = ExcelExportUtil.exportExcel(params,entityList,mapList);
        ExcelChartBuildService.createExcelChart(workbook,graphDefinedList, params.isDynamicData(), params.isAppendGraph());
        
        if (model.containsKey(MapExcelGraphConstants.FILE_NAME)) {
            codedFileName = (String) model.get(MapExcelGraphConstants.FILE_NAME);
        }
        out(workbook, codedFileName, ctx);
    }

}
