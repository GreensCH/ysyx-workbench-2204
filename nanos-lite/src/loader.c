#include <proc.h>
#include <elf.h>

#ifdef __LP64__
# define Elf_Ehdr Elf64_Ehdr
# define Elf_Phdr Elf64_Phdr
#else
# define Elf_Ehdr Elf32_Ehdr
# define Elf_Phdr Elf32_Phdr
#endif

static uintptr_t loader(PCB *pcb, const char *filename) {
  //TODO();
  //pcb参数目前暂不使用, 可以忽略
  //因为ramdisk中目前只有一个文件, filename参数也可以忽略.
  void init_ramdisk();
  size_t ramdisk_read(void *buf, size_t offset, size_t len);
  size_t ramdisk_write(const void *buf, size_t offset, size_t len);
  
  init_ramdisk();
  
  Elf_Ehdr ehdr;
  ramdisk_read(&ehdr, 0, sizeof(Elf_Ehdr));
  assert(*(uint32_t *)ehdr.e_ident == 0x464C457F);//F L E 0x7f
  printf("%d\n",ehdr.e_phnum);

  for(int i = 0; i < ehdr.e_phnum; i++){
    Elf_Phdr phdr;
    ramdisk_read(&phdr, ehdr.e_phentsize*i+ehdr.e_phoff, ehdr.e_phentsize);
    if(phdr.p_type == PT_LOAD){
      ramdisk_write(&(phdr.p_offset), phdr.p_vaddr, phdr.p_memsz);
    }
  }

  
  
  //PT_GNU_STACK	0x6474e551	/* Indicates stack executability */
  
  return 0;
}

void naive_uload(PCB *pcb, const char *filename) {
  uintptr_t entry = loader(pcb, filename);
  Log("Jump to entry = %p", entry);
  ((void(*)())entry) ();
}

