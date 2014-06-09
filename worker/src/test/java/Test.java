import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by piguangtao on 14-4-11.
 */
public class Test {

    public static  void main(String[] args){
        ByteBuf buf = Unpooled.buffer();
        long time = System.currentTimeMillis();
        buf.writeShort((int) (time >> 32));
        buf.writeInt((int) time);

        System.out.println("time:"+time);
        long timestampPre = buf.readUnsignedShort();
        long g = timestampPre << 32;
        int i = buf.readInt();
        System.out.println("after: g: "+ g);
        System.out.println("after: i:"+ i);
        System.out.println("after:"+ (i + g));

        System.out.println("after:"+ ((long)326 << 32));
    }


    public static class JsonTest{


        private String a;
        private String b;
        private String c;

        public JsonTest() {
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("JsonTest{");
            sb.append("a='").append(a).append('\'');
            sb.append(", b='").append(b).append('\'');
            sb.append(", c='").append(c).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
