# ImagePicker 
图片选择器


### 反射获取ImageView对象的私有变量fieldName
```java 
private int getFieldValue(Object object, String fieldName) {
        int val = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            //todo 研究一下这个不加的话会出现什么情况
            field.setAccessible(true);
            int fieldVal = field.getInt(object);
            if (fieldVal > 0 && fieldVal < Integer.MAX_VALUE) {
                val = fieldVal;
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return val;
    }
```

### 获取屏幕尺寸

```java
DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
width = displayMetrics.widthPixels;
height = displayMetrics.heightPixels;
```

### 信号量的使用

```java
Semaphore poolThreadHandlerSemaphore = new Semaphore(0);
//当要获取信号量时，如果没有可用的信号量则阻塞
poolThreadHandlerSemaphore.acquire();
//释放信号量
poolThreadHandlerSemaphore.release();

```

### contentResolver的用法

1. 获得ContentResolver contentResolver = MainActivity.this.getContentResolver();  

2. 获得游标  
    ```java
    Cursor cursor = contentResolver.query(imageUri, null,
                            MediaStore.Images.Media.MIME_TYPE + "=?" + " or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                            new String[]{"image/jpeg", "image/png"},
                            MediaStore.Images.Media.DATE_MODIFIED);
                            
    其中imageUri 为 Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    while (cursor.moveToNext()) {
        //通过游标cursor获取数据库中某一列中的数据
        String path = cursor.getString(
                                cursor.getColumnIndex(MediaStore.Images.Media.DATA));
    }
    ```
3. 关闭游标 cursor.close();
   
### 文件的操作
```java
        List<String> imgList;
        imgList = Arrays.asList(maxDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".JPG")
                        || filename.endsWith(".PNG")
                        || filename.endsWith(".png")
                        || filename.endsWith("jpeg")
                        || filename.endsWith(".JPEG"))
                    return true;
                return false;
            }
        }));
       
```
1. 文件的list方法可以根据Filter来匹配符合规则图片  
2. Arrays.asList 可将数组转化为List


### 在Adapter中如何使Item的View保持原来的转态，例如在Item View 中接入收藏的标签，通过GONE 和 VISIABLE 来实现抱歉的隐藏和显示

思路是在getView方法中记录有收藏标签的position（只要是能唯一表示Item的标示即可），在return convertView之前 判断这个item是否被记录，如果被记录，就显示标签，否则隐藏标签