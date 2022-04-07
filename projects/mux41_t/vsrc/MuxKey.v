`timescale 1ps/1ps

//MuxKey 模块实现了“键值选择”功能，即
//在一个 (键值，数据) 的列表 lut 中，
//根据给定的键值 key ，将 out 设置为与其匹配的数据。
//若列表中不存在键值为 key 的数据，则 out 为 0 。
//特别地， MuxKeyWithDefault 模块可以提供一个默认值 default_out ，
//当列表中不存在键值为 key 的数据，则 out 为 default_out 。
//实例化这两个模块时需要注意如下两点：
//
//  1.需要使用者提供键值对的数量 NR_KEY，键值的位宽 KEY_LEN 
//  以及数据的位宽 DATA_LEN 这三个参数，并保证端口的信号宽度与
//  提供的参数一致，否则将会输出错误的结果
//  2.若列表中存在多个键值为 key 的数据，则 out 的值是未定义的，
//  需要使用者来保证列表中的键值互不相同


module MuxKey #(NR_KEY = 2, KEY_LEN = 1, DATA_LEN = 1) (
  output [DATA_LEN-1:0] out,
  input [KEY_LEN-1:0] key,
  input [NR_KEY*(KEY_LEN + DATA_LEN)-1:0] lut
);
  MuxKeyInternal #(NR_KEY, KEY_LEN, DATA_LEN, 0) i0 (out, key, {DATA_LEN{1'b0}}, lut);
endmodule