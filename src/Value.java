public class Value{

    private final MultimediaFile multimediaFile;

    public Value(){
       this.multimediaFile = new MultimediaFile();
    }

    public Value(MultimediaFile multimediaFile){
       this.multimediaFile = multimediaFile;
    }

    public MultimediaFile getMultimediaFile() {
        return this.multimediaFile;
    }
}