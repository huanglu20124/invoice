package com.hl.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;
import com.hl.exception.InvoiceException;

public interface ModelService {
	public String addModel(HttpServletRequest request) throws InvoiceException;
	public void broadcastAddModelMul(InputStream inputStream, List<ModelAction>batch_list);
	public String deleteModel(Integer user_id, Integer model_id,String user_ip, Integer thread_msg);
	public void broadcastUpdateModel(InputStream inputStream, ModelAction modelAction);
	public void broadcastDeleteModel(InputStream inputStream, ModelAction modelAction);
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer start);
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg);
	public void broadcastClearModel(InputStream inputStream, Integer integer);
	public void rewriteJsonModel()throws Exception;
	public ModelQuery searchModelLabel(Integer page,Integer user_id, String keyword);
	public String uploadModelOrigin(MultipartFile[]files,Integer type,String file_path) throws InvoiceException;
	public String pushBatchModel(String batch_id,Integer thread_msg)throws InvoiceException ;
	public String updateModel(HttpServletRequest request, Integer thread_msg) throws InvoiceException;
	public String cancelAddModel(String file_path);
	public String getModelQueue(String batch_id);
	public String getImgStr(String url)throws InvoiceException;
	public String broadcastAddModelSingle(InputStream inputStream, ModelAction modelAction);
	public String deleteCacheModel(Integer action_id);
	public String updateCacheModel(ModelAction parseObject, String img_str)throws InvoiceException;
}
