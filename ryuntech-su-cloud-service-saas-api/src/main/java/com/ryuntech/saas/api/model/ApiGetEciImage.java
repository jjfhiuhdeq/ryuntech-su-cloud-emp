package com.ryuntech.saas.api.model;


import com.ryuntech.common.model.BaseApi;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author EDZ
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ApiGetEciImage extends BaseApi {


    private String Result;
}
