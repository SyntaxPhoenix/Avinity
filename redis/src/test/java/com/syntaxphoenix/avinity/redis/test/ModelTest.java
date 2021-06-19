package com.syntaxphoenix.avinity.redis.test;

import org.junit.jupiter.api.Test;

import com.syntaxphoenix.avinity.redis.model.RCompound;
import com.syntaxphoenix.avinity.redis.model.RInt;
import com.syntaxphoenix.avinity.redis.model.RList;
import com.syntaxphoenix.avinity.redis.model.RModel;
import com.syntaxphoenix.avinity.redis.model.RType;
import com.syntaxphoenix.avinity.redis.model.io.RIOModel;
import com.syntaxphoenix.avinity.redis.model.io.RNamedModel;

public class ModelTest {
	
	
	@Test
	public void testIO() {
		RCompound compound = new RCompound();
		compound.set("test", (byte) 45);
		compound.set("test2", "This is a test");
		RList<RInt> list = new RList<>(RInt.class);
		list.add(new RInt(534));
		list.add(new RInt(543895));
		compound.set("list", list);
		
		byte[] data = RIOModel.MODEL.write(new RNamedModel("root", compound));
		
		RNamedModel model = RIOModel.MODEL.read(data);
		
		System.out.println(model.getName());
		RModel rmodel = model.getModel();
		System.out.println(rmodel.getType().name());
		if(rmodel.getType() == RType.COMPOUND) {
			RCompound comp = (RCompound) rmodel;
			System.out.println(comp.get("test").getValue());
			System.out.println(comp.get("test2").getValue());
			RList<?> list0 = (RList<?>) comp.get("list");
			for(RModel te : list0) {
				System.out.println(te.getValue());
			}
		}
	}

}
