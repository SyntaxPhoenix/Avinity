package com.syntaxphoenix.avinity.redis.model.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.Function;

import com.syntaxphoenix.avinity.redis.model.*;
import com.syntaxphoenix.syntaxapi.data.DataAdapter;
import com.syntaxphoenix.syntaxapi.utils.java.Primitives;

public class RedisAdapter<P, C extends RModel> extends DataAdapter<P, C, RModel> {

	protected RedisAdapter(Class<P> primitiveType, Class<C> resultType, Function<P, C> builder, Function<C, P> extractor) {
		super(primitiveType, resultType, builder, extractor);
	}

	@Override
	public Class<RModel> getBaseType() {
		return RModel.class;
	}

	/*
	 * 
	 */
	
	protected static DataAdapter<?, ?, RModel> createAdapter(Class<?> type) {
		type = Primitives.fromPrimitive(type);

		/*
		 * Text and State
		 */

		if (Objects.equals(String.class, type))
			return new RedisAdapter<String, RString>(String.class, RString.class, RString::new, RString::getValue);

		if (Objects.equals(Boolean.class, type))
			return new RedisAdapter<Boolean, RBoolean>(Boolean.class, RBoolean.class, RBoolean::new, RBoolean::getValue);

		/*
		 * Numbers
		 */

		if (Objects.equals(Byte.class, type))
			return new RedisAdapter<Byte, RByte>(Byte.class, RByte.class, RByte::new, RByte::getValue);

		if (Objects.equals(Short.class, type))
			return new RedisAdapter<Short, RShort>(Short.class, RShort.class, RShort::new, RShort::getValue);

		if (Objects.equals(Integer.class, type))
			return new RedisAdapter<Integer, RInt>(Integer.class, RInt.class, RInt::new, RInt::getValue);

		if (Objects.equals(Long.class, type))
			return new RedisAdapter<Long, RLong>(Long.class, RLong.class, RLong::new, RLong::getValue);

		if (Objects.equals(BigInteger.class, type))
			return new RedisAdapter<BigInteger, RBigInteger>(BigInteger.class, RBigInteger.class, RBigInteger::new, RBigInteger::getValue);

		if (Objects.equals(Float.class, type))
			return new RedisAdapter<Float, RFloat>(Float.class, RFloat.class, RFloat::new, RFloat::getValue);

		if (Objects.equals(Double.class, type))
			return new RedisAdapter<Double, RDouble>(Double.class, RDouble.class, RDouble::new, RDouble::getValue);

		if (Objects.equals(BigDecimal.class, type))
			return new RedisAdapter<BigDecimal, RBigDecimal>(BigDecimal.class, RBigDecimal.class, RBigDecimal::new, RBigDecimal::getValue);

		/*
		 * Number Arrays
		 */

		if (Objects.equals(byte[].class, type))
			return new RedisAdapter<byte[], RByteArray>(byte[].class, RByteArray.class, RByteArray::new, RByteArray::getValue);

		if (Objects.equals(short[].class, type))
			return new RedisAdapter<short[], RShortArray>(short[].class, RShortArray.class, RShortArray::new, RShortArray::getValue);
		
		if (Objects.equals(int[].class, type))
			return new RedisAdapter<int[], RIntArray>(int[].class, RIntArray.class, RIntArray::new, RIntArray::getValue);

		if (Objects.equals(long[].class, type))
			return new RedisAdapter<long[], RLongArray>(long[].class, RLongArray.class, RLongArray::new, RLongArray::getValue);
		
		if (Objects.equals(float[].class, type))
			return new RedisAdapter<float[], RFloatArray>(float[].class, RFloatArray.class, RFloatArray::new, RFloatArray::getValue);
		
		if (Objects.equals(double[].class, type))
			return new RedisAdapter<double[], RDoubleArray>(double[].class, RDoubleArray.class, RDoubleArray::new, RDoubleArray::getValue);

		/*
		 * RModel
		 */

		if (RModel.class.isAssignableFrom(type))
			return new RedisAdapter<RModel, RModel>(RModel.class, RModel.class, model -> model, model -> model);

		return null;
	}

}
