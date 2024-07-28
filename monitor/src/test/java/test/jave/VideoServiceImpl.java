//package test.video;
//
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.web.multipart.MultipartFile;
//import ws.schild.jave.Encoder;
//import ws.schild.jave.EncoderException;
//import ws.schild.jave.MultimediaObject;
//import ws.schild.jave.encode.AudioAttributes;
//import ws.schild.jave.encode.EncodingAttributes;
//import ws.schild.jave.encode.VideoAttributes;
//import ws.schild.jave.filtergraphs.OverlayWatermark;
//import ws.schild.jave.filters.helpers.OverlayLocation;
//import ws.schild.jave.info.AudioInfo;
//import ws.schild.jave.info.MultimediaInfo;
//import ws.schild.jave.info.VideoInfo;
//import ws.schild.jave.utils.AutoRemoveableFile;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class VideoServiceImpl
//{
//
//    protected int maxDuration=2;
//    protected File waterMark;
//    public void post() throws IOException {
//        final String fileName = "water_mark.png";
//        final String tempDir = System.getProperty("java.io.tmpdir");
//        waterMark = new File(tempDir,fileName);
//        final Path path = waterMark.toPath();
//        try{
//            Files.delete(path);
//        }catch(Throwable t){
//        }
//        //        Thumbnails.Builder<File> srcFileBuilder = Thumbnails.of(waterMark).scale(0.8f);
//        final int alpha = 255/2;
//        final InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
//        final BufferedImage image = Thumbnails.of(is).scale(0.5).asBufferedImage();
//        final int width = image.getWidth();
//        final int height = image.getHeight();
//        BufferedImage output = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
//        final Graphics2D graphics = output.createGraphics();
//        output = graphics.getDeviceConfiguration().createCompatibleImage(width,height,Transparency.TRANSLUCENT);
//        for(int i=output.getMinY();i<output.getHeight();i++){
//            for(int j=output.getMinX();j<output.getWidth();j++){
//                final int srcRgb = image.getRGB(j, i);
//                final int srcAlpha = srcRgb&0xff000000;
//                if(0!=srcAlpha){
//                    final int rgb = (alpha<<24)|(srcRgb&0x00ffffff);
//                    output.setRGB(j,i,rgb);
//                }
//            }
//        }
//        graphics.setComposite(AlphaComposite.SrcIn);
//        graphics.drawImage(image,0,0,width,height,null);
//        graphics.dispose();
//        ImageIO.write(output,"png",waterMark);
//        is.close();
//    }
//    static class UploadCtx{
//        public MultipartFile inputFile;
//        public LocalDate yyyyMMdd;
//        public File dest;
//        public File destParent;
//        public String fileType;
//        public AutoRemoveableFile origin;
//        public LocalDateTime now;
//
//        private String fileId;
//        public String requireId(){
//            if(Objects.isNull(fileId)){
//                fileId = UUID.randomUUID().toString();
//            }
//            return fileId;
//        }
//        public String fileName(){
//            return String.format("%s.%s",requireId(),fileType);
//        }
//
//        public void close() {
//            if(!Objects.isNull(origin)){
//                origin.close();
//            }
//        }
//    }
//
//    public Object upload(MultipartFile file, Object... params) {
//        final UploadCtx ctx = new UploadCtx();
//        try{
//            ctx.inputFile = file;
//            ctx.now = LocalDateTime.now();
//            ctx.yyyyMMdd = ctx.now.toLocalDate();
//            ctx.fileType = "mp4";
//            try {
//                saveOriginVideo(ctx);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                saveToDisk(ctx);
//            } catch (EncoderException e) {
//                throw new RuntimeException(e);
//            }
//            saveInfoToDatabase(ctx);
//            return ctx.fileId;
//        } finally{
//            ctx.close();
//        }
//    }
//
//    public Object updateVideoData(Object... params) {
//        final Long projId = (Long)params[0];
//        final String videoFileId = (String)params[1];
////        ProjectBill older = projectBillDomain.selectById(projId);
////        final ProjectBill updater = BeanUtil.copyProperties(older,ProjectBill.class);
////        updater.setExtStr11(videoFileId);
////        return  projectBillDomain.update(older,updater);
//        return null;
//    }
//
//    private void saveInfoToDatabase(UploadCtx ctx) {
//        final MultipartFile inputFile = ctx.inputFile;
//        final File dest = ctx.dest;
////        final SysFile inserter = new SysFile();
////        inserter.setId(ctx.requireId());
////        inserter.setFileDesc(inputFile.getOriginalFilename());
////        inserter.setFileName(ctx.fileName());
////        inserter.setTargetDate(ctx.yyyyMMdd);
////        inserter.setFilePath(dest.getPath());
////        inserter.setCreateTime(ctx.now);
////        inserter.setFileSize(FileUtil.getPrintSize(dest.length()));
////        inserter.setFileType(ctx.fileType);
////        sysFileMapper.insert(inserter);
//    }
//    protected int MAX_BIT_RATE_OF_VIDEO = 2468000/2;
//    /**
//     * 帧每秒
//     */
//    protected int MAX_FRAME_RATE_OF_VIDEO = 25;
//    protected int MAX_BIT_RATE_OF_AUDIO = 2000;
//    protected int MAX_SAMPLING_RATE_OF_AUDIO = 22050;
//    private void saveToDisk(UploadCtx ctx) throws EncoderException {
//        final String  videoCodec = "h264";
//        //aac 后续 尝试下aac 音频格式 ，IOS 手机 微信公众号 浏览器播放视频 没有声音
////        final String audioCodec = "libmp3lame";
//        final String audioCodec = "aac";
//        final Encoder encoder = new Encoder();
//        final MultimediaObject mo = new MultimediaObject(ctx.origin);
//        final MultimediaInfo info = mo.getInfo();
//        checkDuration(info);
//        final VideoInfo vi = info.getVideo();
//        final AudioInfo ai = info.getAudio();
//        final int bitRateOfVideo = vi.getBitRate();
//        final EncodingAttributes att = new EncodingAttributes();
//        final int coreNum = Runtime.getRuntime().availableProcessors();
////        System.out.println("core Num:"+coreNum);//4
//        att.setDecodingThreads(coreNum);
//        att.setEncodingThreads(coreNum);
////        att.setFilterThreads(coreNum);//
//        att.setOutputFormat(ctx.fileType);
//        if(!Objects.isNull(ai)){
////            final int bitRateOfAudio = ai.getBitRate();
////            final int samplingRateOfAudio = ai.getSamplingRate();
//            final AudioAttributes audio = new AudioAttributes();
////            audio.setBitRate(Math.min(MAX_BIT_RATE_OF_AUDIO,bitRateOfAudio));
////            audio.setChannels(1);
////            audio.setSamplingRate(Math.min(MAX_SAMPLING_RATE_OF_AUDIO,samplingRateOfAudio));
//            audio.setCodec(audioCodec);
////            audio.setVolume(256);
//            att.setAudioAttributes(audio);
//        }
//        final VideoAttributes video = new VideoAttributes();
//        video.setFaststart(true);
//        video.setCodec(videoCodec);
//        video.setFrameRate(MAX_FRAME_RATE_OF_VIDEO);
//        video.setBitRate(Math.min(MAX_BIT_RATE_OF_VIDEO,bitRateOfVideo));
//        final OverlayWatermark overlayWatermark = new OverlayWatermark(waterMark, OverlayLocation.TOP_LEFT, 20, 20);
//        video.addFilter(overlayWatermark);
//        att.setVideoAttributes(video);
//        final String destFileName = ctx.fileName();
////        final String parentPathStr = uploadProperty.getUploadPath()+ ctx.yyyyMMdd;
//        final String parentPathStr = "/tmp/file/"+ ctx.yyyyMMdd;
//        final File dest = new File(parentPathStr,destFileName);
//        final File parentFile = dest.getParentFile();
//        if(!parentFile.exists()){
//            parentFile.mkdirs();
//        }
//        if(dest.isAbsolute()&&!dest.exists()){
//            encoder.encode(mo,dest,att);
////                Files.copy(src.toPath(),Files.newOutputStream(dest.toPath()));
//        }else{
//            throw new RuntimeException("写入磁盘异常,dest file:"+dest);
//        }
////        System.out.println(String.format("decoding threads:%s,encoding threads:%s,filtering threads:%s"
////                ,att.getDecodingThreads(),att.getEncodingThreads(),att.getFilterThreads()));
//        ctx.dest = dest;
//        ctx.destParent = parentFile;
//    }
//
//    private void checkDuration(MultimediaInfo info) {
//        throw new RuntimeException();
////        BusinessException.doThrow(info.getDuration()>maxDuration*60*1000,String.format("视频时长不允许超过%s分钟",maxDuration));
//    }
//
//    private void saveOriginVideo(UploadCtx ctx) throws IOException {
//        final String tmpdir = System.getProperty("java.io.tmpdir");
//        final AutoRemoveableFile f = new AutoRemoveableFile(new File(tmpdir),ctx.fileName());
//        ctx.inputFile.transferTo(f);
//        ctx.origin = f;
//    }
//
//    public static void main(String[] args) throws EncoderException {
//        List<String> collect = Arrays.stream(new Encoder().getAudioEncoders()).collect(Collectors.toList());
//        /*
//        aac, ac3, ac3_fixed, adpcm_adx, adpcm_argo
//        , g722, g726, g726le, adpcm_ima_alp, adpcm_ima_amv
//        , adpcm_ima_apm, adpcm_ima_qt, adpcm_ima_ssi
//        , adpcm_ima_wav, adpcm_ms, adpcm_swf, adpcm_yamaha
//        , alac, libopencore_amrnb, libvo_amrwbenc, aptx
//        , aptx_hd, comfortnoise, dca, eac3, flac, g723_1
//        , libgsm, libgsm_ms, mlp, mp2, mp2fixed, libmp3lame
//        , nellymoser, opus, libopus, pcm_alaw, pcm_dvd, pcm_f32be
//        , pcm_f32le, pcm_f64be, pcm_f64le, pcm_mulaw, pcm_s16be
//        , pcm_s16be_planar, pcm_s16le, pcm_s16le_planar
//        , pcm_s24be, pcm_s24daud, pcm_s24le, pcm_s24le_planar
//        , pcm_s32be, pcm_s32le, pcm_s32le_planar, pcm_s64be
//        , pcm_s64le, pcm_s8, pcm_s8_planar, pcm_u16be
//        , pcm_u16le, pcm_u24be, pcm_u24le, pcm_u32be
//        , pcm_u32le, pcm_u8, pcm_vidc, real_144, roq_dpcm
//        , s302m, sbc, sonic, sonicls, libspeex, truehd, tta
//        , vorbis, libvorbis, wavpack, wmav1, wmav2
//         */
//        System.out.println(collect);
//    }
//}
