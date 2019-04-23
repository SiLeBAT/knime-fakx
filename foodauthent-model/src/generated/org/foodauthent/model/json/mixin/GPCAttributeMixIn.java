/*
 * TODO	
 */
package org.foodauthent.model.json.mixin;

import org.foodauthent.model.GPCAttributeValue;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;



import org.foodauthent.model.GPCAttribute;
import org.foodauthent.model.GPCAttribute.GPCAttributeBuilder;

/**
 * MixIn class for entity implementations that adds jackson annotations for de-/serialization.
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "",
    visible = true,
    defaultImpl = GPCAttribute.class)
@JsonSubTypes({
    @Type(value = GPCAttribute.class, name="GPCAttribute")
})
@JsonDeserialize(builder=GPCAttributeBuilder.class)
@javax.annotation.Generated(value = "org.foodauthent.codegen.FoodAuthentCodegen")
public interface GPCAttributeMixIn {

	@JsonIgnore
	public long getPersistenceId();
	
    @JsonIgnore
    public String getTypeID();
    
   	@JsonIgnore
  	public UUID getFaId();
    

    @JsonProperty("code")
    public String getCode();
    
    @JsonProperty("text")
    public String getText();
    
    @JsonProperty("values")
    public java.util.List<GPCAttributeValue> getValues();
    

    /**
     * MixIn class for entity builder implementations that adds jackson annotations for the de-/serialization.
     *
     * @author Martin Horn, University of Konstanz
     */
    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "",
        defaultImpl = GPCAttributeBuilder.class)
    @JsonSubTypes({
        @Type(value = GPCAttribute.GPCAttributeBuilder.class, name="GPCAttribute")
    })
    // AUTO-GENERATED CODE; DO NOT MODIFY
    public static interface GPCAttributeMixInBuilder {
    
        public GPCAttributeMixIn build();
    
        @JsonProperty("code")
        public GPCAttributeMixInBuilder setCode(final String code);
        
        @JsonProperty("text")
        public GPCAttributeMixInBuilder setText(final String text);
        
        @JsonProperty("values")
        public GPCAttributeMixInBuilder setValues(final java.util.List<GPCAttributeValue> values);
        
    }


}

